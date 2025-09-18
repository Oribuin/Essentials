package dev.oribuin.essentials.manager;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.config.impl.MySQLConfig;
import dev.oribuin.essentials.database.AddonRepository;
import dev.oribuin.essentials.database.connector.DatabaseConnector;
import dev.oribuin.essentials.database.connector.MySQLConnector;
import dev.oribuin.essentials.database.connector.SQLiteConnector;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class DataManager {

    private static final Map<Class<? extends AddonRepository>, AddonRepository> repositories = new HashMap<>();
    private final EssentialsPlugin plugin;
    private DatabaseConnector connector;

    public DataManager(EssentialsPlugin plugin) {
        this.plugin = plugin;
        this.reload();
    }

    /**
     * Reload all the data manager, refreshing all connections
     */
    public void reload() {
        MySQLConfig sqlConfig = MySQLConfig.get();
        if (sqlConfig.isEnabled()) {
            String hostname = sqlConfig.getHostname();
            int port = sqlConfig.getPort();
            String database = sqlConfig.getDatabaseName();
            String username = sqlConfig.getUsername();
            String password = sqlConfig.getPassword();
            boolean useSSL = sqlConfig.useSSL();
            int poolSize = sqlConfig.getConnectionPoolSize();

            this.connector = new MySQLConnector(this.plugin, hostname, port, database, username, password, useSSL, poolSize);
            this.plugin.getLogger().info("Data manager connected using MySQL.");
        } else {
            this.connector = new SQLiteConnector(this.plugin);
            this.connector.cleanup();
            this.plugin.getLogger().info("Data manager connected using SQLite.");
        }
    }

    public void disable() {
        if (this.connector == null) return;

        // Wait for all connections to finish
        long now = System.currentTimeMillis();
        long deadline = now + 5000;
        synchronized (this.connector.getLock()) {
            while (!this.connector.isFinished() && now < deadline) {
                try {
                    this.connector.getLock().wait(deadline - now);
                    now = System.currentTimeMillis();
                } catch (InterruptedException ex) {
                    this.plugin.getLogger().severe("Interrupted error occurred: " + ex.getMessage());
                }
            }
        }

        for (AddonRepository repository : repositories.values()) {
            repository.unload();
        }
        this.connector.closeConnection();
    }

    public final boolean isConnected() {
        return this.connector != null;
    }

    /**
     * Create a new repository instance and add it to the map
     * You will need to manually load the repository after creating it
     *
     * @param repository The repository class
     */
    public static <T extends AddonRepository> T create(Class<T> repository) {
        try {
            DatabaseConnector connector = EssentialsPlugin.getInstance().getDataManager().getConnector();

            T repo = repository.getConstructor(DatabaseConnector.class).newInstance(connector);
            repositories.put(repository, repo);
            return repo;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            Bukkit.getLogger().severe("Failed to create repository: " + repository.getSimpleName() + " - " + e.getMessage());
        }

        return null;
    }

    /**
     * Get a repository from the map
     *
     * @param clazz The class of the repository
     * @param <T>   The repository type
     *
     * @return The repository
     */
    public static <T extends AddonRepository> T repository(Class<T> clazz) {
        return clazz.cast(repositories.get(clazz));
    }

    public DatabaseConnector getConnector() {
        return connector;
    }
}
