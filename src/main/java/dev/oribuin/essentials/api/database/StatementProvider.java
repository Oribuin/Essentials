package dev.oribuin.essentials.api.database;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.api.database.serializer.DataType;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StatementProvider {

    private final StatementType type;
    private final Connection connection;
    private final Map<String, DataColumn<?>> columns; // The columns to use for the statement
    private final List<String> primaryKeys; // The primary keys for the table
    private String table; // The table to use
    private int limit; // Used for SELECT statements
    private boolean autoIncrement; // If the primary key should auto increment or not

    /**
     * Create a new StatementProvider instance with a type and table
     *
     * @param type The type of statement
     */
    private StatementProvider(StatementType type, Connection connection) {
        this.type = type;
        this.connection = connection;
        this.table = null;
        this.limit = -1;
        this.columns = new HashMap<>();
        this.primaryKeys = new ArrayList<>();
        this.autoIncrement = false;

        try {
            if (this.connection != null && !this.connection.getAutoCommit()) {
                this.connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            EssentialsPlugin.get().getLogger().severe("Failed to establish auto commit: " + ex.getMessage());
        }
    }

    /**
     * Create a new statement provider with a specific type of statement type
     *
     * @param type      The type of statement
     * @param connector The database connector to use
     *
     * @return The statement provider
     */
    public static StatementProvider create(StatementType type, DatabaseConnector connector) {
        try {
            return new StatementProvider(type, connector.connect());
        } catch (SQLException ex) {
            EssentialsPlugin.get().getLogger().severe("There was an issue establishing a ConnectionProvider: " + ex.getMessage());
            return new StatementProvider(type, null);
        }
    }

    /**
     * Create a new statement provider with a specific type of statement type
     *
     * @param type       The type of statement
     * @param repository The module repository using the statement provider
     *
     * @return The statement provider
     */
    public static StatementProvider create(StatementType type, ModuleRepository repository) {
        try {
            return new StatementProvider(type, repository.connector.connect()).table(repository.table);
        } catch (SQLException ex) {
            EssentialsPlugin.get().getLogger().severe("There was an issue establishing a ConnectionProvider: " + ex.getMessage());
            return new StatementProvider(type, null);
        }
    }

    /**
     * Add a column to the statement provider to use
     *
     * @param name  The name of the column
     * @param type  The type of the column
     * @param value The value of the column
     *
     * @return The statement provider
     */
    public <T> StatementProvider column(String name, DataType<T> type, T value) {
        this.columns.put(name, DataColumn.of(name, type, value));
        return this;
    }

    /**
     * Add a column to the statement provider to use
     *
     * @param name The name of the column
     * @param type The type of the column
     *
     * @return The statement provider
     */
    public <T> StatementProvider column(String name, DataType<T> type) {
        this.columns.put(name, DataColumn.of(name, type, null));
        return this;
    }

    /**
     * Add a column to the statement provider to use
     *
     * @param name  The name of the column
     * @param type  The type of the column
     * @param value The value of the column
     *
     * @return The statement provider
     */
    public <T> StatementProvider column(String name, DataType<T> type, T value, boolean nullable) {
        this.columns.put(name, DataColumn.of(name, type, value, nullable));
        return this;
    }

    /**
     * Add a column to the statement provider to use
     *
     * @param name     The name of the column
     * @param type     The type of the column
     * @param nullable If the column is nullable
     *
     * @return The statement provider
     */
    public <T> StatementProvider column(String name, DataType<T> type, boolean nullable) {
        this.columns.put(name, DataColumn.of(name, type, null, nullable));
        return this;
    }

    @Nullable
    public QueryResult executeSync() throws NullPointerException {
        // Make sure the table is not null before executing the statement
        if (this.table == null) {
            throw new NullPointerException("Table does not exist");
        }

        // Make sure the connection is not null before executing the statement
        if (this.connection == null) {
            throw new NullPointerException("Connection for " + this.table + " type [" + this.type + "] does not exist");
        }

        // Create the table if it does not exist
        return switch (this.type) {
            case UPDATE -> this.update();
            case DELETE -> this.delete();
            case INSERT -> this.insert();
            case SELECT -> this.select();

            case DROP_TABLE -> {
                this.dropTable();
                yield null;
            }

            case CREATE_TABLE -> {
                this.createTable();
                yield null;
            }
        };
    }

    /**
     * Create a new statement provider with a specific type of statement type
     */
    public CompletableFuture<QueryResult> execute() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.executeSync();
            } catch (NullPointerException ex) {
                EssentialsPlugin.get().getLogger().severe("Failed to execute SQL Statement: " + ex.getMessage());
                return null;
            }
        });
    }

    /**
     * Create a table in the database if it does not exist already
     */
    private void createTable() {
        StringBuilder statement = new StringBuilder("CREATE TABLE IF NOT EXISTS " + this.table + " (");

        // Append the columns to the statement
        String columns = this.columns.values()
                .stream()
                .map(DataColumn::construct)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");

        // Append the columns to the statement
        statement.append(columns);

        if (!this.primaryKeys.isEmpty()) {
            statement.append(", ").append(this.constructPrimary());
        }

        statement.append(")");

        // Execute the statement
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(statement.toString())) {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            EssentialsPlugin.get().getLogger().severe("An error occurred while creating the table: " + e.getMessage());
            EssentialsPlugin.get().getLogger().severe("Full Query: " + statement);

        }
    }

    /**
     * Select from the table
     *
     * @return The result set from the select statement
     */
    private QueryResult select() {
        StringBuilder statement = new StringBuilder("SELECT * FROM " + this.table);

        // Append the limit to the statement
        if (this.limit > 0) {
            statement.append(" LIMIT ").append(this.limit);
        }

        // Add columns aa "WHERE" statement to the query
        if (!this.columns.isEmpty()) {
            statement.append(" WHERE ");
            statement.append(this.columns.values()
                    .stream()
                    .map(column -> column.name() + " = ?")
                    .reduce((s1, s2) -> s1 + " AND " + s2)
                    .orElse("")
            );
        }

        // Execute the statement
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(statement.toString())) {
            int index = 1;

            // Serialize the columns to the prepared statement
            for (DataColumn<?> column : this.columns.values()) {
                column.serialize(preparedStatement, index++);
            }

            return new QueryResult(preparedStatement.executeQuery());
        } catch (Exception e) {
            EssentialsPlugin.get().getLogger().severe("An error occurred while selecting from the table: " + e.getMessage());
            return null;
        }
    }

    /**
     * Insert into the table
     *
     * @return The result set from the insert statement
     */
    private QueryResult insert() {
        StringBuilder statement = new StringBuilder("REPLACE INTO " + this.table + " (");

        // Append the columns to the statement
        String columns = this.columns.values()
                .stream()
                .map(DataColumn::name)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");

        statement.append(columns).append(") VALUES (");

        // Append the values to the statement
        String values = this.columns.values()
                .stream()
                .map(column -> "?")
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");

        statement.append(values).append(")");

        // Execute the statement
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(statement.toString())) {
            int index = 1;

            // Serialize the columns to the prepared statement
            for (DataColumn<?> column : this.columns.values()) {
                column.serialize(preparedStatement, index++);
            }

            preparedStatement.executeUpdate();
            return new QueryResult(preparedStatement.getGeneratedKeys());
        } catch (Exception e) {
            EssentialsPlugin.get().getLogger().severe("An error occurred while inserting into the table: " + e.getMessage());
            return null;
        }
    }

    /**
     * Update the table where the columns match the values provided
     *
     * @return The result set from the update statement
     */
    private QueryResult update() {
        if (this.columns.isEmpty()) {
            EssentialsPlugin.get().getLogger().severe("Columns cannot be empty for type " + this.type.name());
            return null;
        }

        StringBuilder statement = new StringBuilder("UPDATE " + this.table + " SET ");

        // Append the columns to the statement
        String columns = this.columns.values()
                .stream()
                .map(column -> column.name() + " = ?")
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");

        statement.append(columns);

        // Add columns aa "WHERE" statement to the query
        statement.append(" WHERE ");
        statement.append(this.columns.values()
                .stream()
                .map(column -> column.name() + " = ?")
                .reduce((s1, s2) -> s1 + " AND " + s2)
                .orElse("")
        );

        // Execute the statement
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(statement.toString())) {
            int index = 1;

            // Serialize the columns to the prepared statement
            for (DataColumn<?> column : this.columns.values()) {
                column.serialize(preparedStatement, index++);
            }

            preparedStatement.executeUpdate();
            return new QueryResult(preparedStatement.getGeneratedKeys());
        } catch (Exception e) {
            EssentialsPlugin.get().getLogger().severe("An error occurred while updating the table: " + e.getMessage());
            return null;
        }
    }

    /**
     * Delete from the table where the columns match the values provided
     *
     * @return The result set from the delete statement
     */
    private QueryResult delete() {
        if (this.columns.isEmpty()) {
            EssentialsPlugin.get().getLogger().severe("Columns cannot be empty for type " + this.type.name());
            return null;
        }

        // Add columns aa "WHERE" statement to the query
        String statement = "DELETE FROM " + this.table + " WHERE " + this.columns.values()
                .stream()
                .map(column -> column.name() + " = ?")
                .reduce((s1, s2) -> s1 + " AND " + s2)
                .orElse("");

        // Execute the statement
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(statement)) {
            int index = 1;

            // Serialize the columns to the prepared statement
            for (DataColumn<?> column : this.columns.values()) {
                column.serialize(preparedStatement, index++);
            }

            preparedStatement.executeUpdate();
            return new QueryResult(preparedStatement.getGeneratedKeys());
        } catch (Exception e) {
            EssentialsPlugin.get().getLogger().severe("An error occurred while delete from the table: " + e.getMessage());
            return null;
        }
    }

    /**
     * Drop the table from the database if it exists already
     */
    private void dropTable() {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement("DROP TABLE IF EXISTS " + this.table)) {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            EssentialsPlugin.get().getLogger().severe("An error occurred while dropping the table: " + e.getMessage());
        }
    }

    /**
     * @return Create the columns for the statement provider to use
     */
    private String constructPrimary() {
        if (this.primaryKeys.isEmpty()) throw new IllegalStateException("Primary keys cannot be empty when constructing the primary key");

        String primary = this.primaryKeys.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        return String.format("PRIMARY KEY (%s)", primary);
    }

    /**
     * Set the columns for the statement provider
     *
     * @param table The table to use
     *
     * @return The statement provider
     */
    public StatementProvider table(String table) {
        this.table = table;
        return this;
    }

    /**
     * Set the limit for the statement provider
     *
     * @param limit The limit to use
     *
     * @return The statement provider
     */
    public StatementProvider limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Add primary keys for the table
     *
     * @param names The names of the primary keys
     *
     * @return The statement provider
     */
    public StatementProvider primary(String... names) {
        this.primaryKeys.addAll(List.of(names));
        return this;
    }

    /**
     * Set the primary key for the table
     *
     * @param name The name of the primary key
     *
     * @return The statement provider
     */
    public StatementProvider primaryAuto(String name) {
        this.autoIncrement = true;
        return this.primary(name);
    }

}
