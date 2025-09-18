package dev.oribuin.essentials.database.connector;

import dev.oribuin.essentials.EssentialsPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("deprecation")
public class SQLiteConnector implements DatabaseConnector {

    private final EssentialsPlugin javaPlugin;
    private final String connectionString;
    private Connection connection;
    private final AtomicInteger openConnections;
    private final Object lock;

    public SQLiteConnector(EssentialsPlugin javaPlugin) {
        this(javaPlugin, javaPlugin.getDescription().getName().toLowerCase());
    }

    public SQLiteConnector(EssentialsPlugin javaPlugin, String dbName) {
        this.javaPlugin = javaPlugin;
        this.connectionString = "jdbc:sqlite:" + javaPlugin.getDataFolder() + File.separator + dbName + ".db";
        this.openConnections = new AtomicInteger();
        this.lock = new Object();

        try {
            Class.forName("org.sqlite.JDBC"); // Make sure the driver is actually loaded

            // We often find that the /var/tmp directory is set to noexec which breaks our plugins.
            // This moves the temp directory to somewhere we know will absolutely have exec permissions.
            // If this gets overridden by another plugin or something else, that's also fine.
            File tmpdir = new File(this.javaPlugin.getDataFolder(), "tmp");
            if (!tmpdir.exists())
                tmpdir.mkdirs();
            System.setProperty("org.sqlite.tmpdir", tmpdir.getAbsolutePath());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (SQLException ex) {
            this.javaPlugin.getLogger().severe("An error occurred closing the SQLite database connection: " + ex.getMessage());
        }
    }

    @Override
    public void connect(ConnectionCallback callback) {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.connection = DriverManager.getConnection(this.connectionString);
                this.connection.setAutoCommit(false);
            }
        } catch (SQLException ex) {
            this.javaPlugin.getLogger().severe("An error occurred retrieving the SQLite database connection: " + ex.getMessage());
        }

        this.openConnections.incrementAndGet();
        try {
            if (this.connection.getAutoCommit())
                this.connection.setAutoCommit(false);

            callback.accept(this.connection);
            try {
                this.connection.commit();
            } catch (SQLException e) {
                if (e.getMessage() != null && !e.getMessage().contains("transaction"))
                    throw e;

                try {
                    this.connection.rollback();
                } catch (SQLException ignored) {
                }
            }
        } catch (Exception ex) {
            this.javaPlugin.getLogger().severe("An error occurred executing an SQLite query: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            int open = this.openConnections.decrementAndGet();
            synchronized (this.lock) {
                if (open == 0)
                    this.lock.notify();
            }
        }
    }

    @Override
    public void connect(ConnectionCallback callback, boolean useTransaction) {
        if (useTransaction) {
            this.connect(callback);
            return;
        }

        try {
            if (this.connection == null || this.connection.isClosed())
                this.connection = DriverManager.getConnection(this.connectionString);
        } catch (SQLException ex) {
            this.javaPlugin.getLogger().severe("An error occurred retrieving the SQLite database connection: " + ex.getMessage());
        }

        this.openConnections.incrementAndGet();
        try {
            if (!this.connection.getAutoCommit())
                this.connection.setAutoCommit(true);

            callback.accept(this.connection);
        } catch (Exception ex) {
            this.javaPlugin.getLogger().severe("An error occurred executing an SQLite query: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            int open = this.openConnections.decrementAndGet();
            synchronized (this.lock) {
                if (open == 0)
                    this.lock.notify();
            }
        }
    }

    @Override
    public Connection connect() {
        try {
            if (this.connection == null || this.connection.isClosed())
                this.connection = DriverManager.getConnection(this.connectionString);
        } catch (SQLException ex) {
            this.javaPlugin.getLogger().severe("An error occurred retrieving the SQLite database connection: " + ex.getMessage());
        }
        return this.connection;
    }

    @Override
    public Object getLock() {
        return this.lock;
    }

    @Override
    public boolean isFinished() {
        return this.openConnections.get() == 0;
    }

    @Override
    public void cleanup() {
        this.connect(connection -> {
            try {
                connection.createStatement().execute("VACUUM");
            } catch (Exception e) {
                this.javaPlugin.getLogger().warning("Failed to run vacuum on database, unable to access temp directory: no read/write access.");
            }
        }, false);
    }

}

