package dev.oribuin.essentials.api.database;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.api.database.serializer.DataType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    }

    /**
     * Create a new statement provider with a specific type of statement type
     *
     * @param type The type of statement
     *
     * @return The statement provider
     */
    public static StatementProvider create(StatementType type, Connection connection) {
        return new StatementProvider(type, connection);
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

    /**
     * Create a new statement provider with a specific type of statement type
     *
     * @return The statement provider
     */
    public CompletableFuture<ResultSet> execute() {
        // Make sure the table is not null before executing the statement
        if (this.table == null) return CompletableFuture.failedFuture(new IllegalStateException("Table cannot be null"));

        // Make sure the connection is not null before executing the statement
        if (this.connection == null) return CompletableFuture.failedFuture(new IllegalStateException("Connection cannot be null"));

        // Make sure the columns are not null before executing the statement
        if (this.columns == null || columns.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalStateException("Columns cannot be null or empty"));
        }

        // Create the table if it does not exist
        return switch (this.type) {
            case CREATE_TABLE -> this.createTable(this.connection).thenApply(b -> null);
            case DROP_TABLE -> this.dropTable().thenApply(b -> null);
            case SELECT -> this.select();
            case INSERT -> this.insert();
            case UPDATE -> this.update();
            case DELETE -> this.delete();
        };
    }

    /**
     * Create a table in the database if it does not exist already
     *
     * @param connection The connection to use
     *
     * @return If the table was created successfully
     */
    private CompletableFuture<Boolean> createTable(Connection connection) {
        if (this.table == null) return CompletableFuture.failedFuture(new IllegalStateException("Table cannot be null"));

        return CompletableFuture.supplyAsync(() -> {
            StringBuilder statement = new StringBuilder("CREATE TABLE IF NOT EXISTS " + this.table + " (");

            // Append the columns to the statement
            String columns = this.columns.values()
                    .stream()
                    .map(DataColumn::construct)
                    .reduce((s1, s2) -> s1 + ", " + s2)
                    .orElse("");

            // Append the columns to the statement
            statement.append(columns);
            statement.append(this.primaryKeys.isEmpty() ? ")" : this.constructPrimary());

            // Execute the statement
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement.toString())) {
                preparedStatement.executeUpdate();
                return true;
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while creating the table", e);
            }
        });

    }

    /**
     * Select from the table
     *
     * @return The result set from the select statement
     */
    public CompletableFuture<ResultSet> select() {
        if (this.table == null) return CompletableFuture.failedFuture(new IllegalStateException("Table cannot be null"));

        return CompletableFuture.supplyAsync(() -> {
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

            try (PreparedStatement preparedStatement = this.connection.prepareStatement(statement.toString())) {
                int index = 1;

                // Serialize the columns to the prepared statement
                for (DataColumn<?> column : this.columns.values()) {
                    column.serialize(preparedStatement, index++);
                }

                return preparedStatement.executeQuery();
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while selecting from the table", e);
            }
        });
    }

    /**
     * Insert into the table
     *
     * @return The result set from the insert statement
     */
    public CompletableFuture<ResultSet> insert() {
        if (this.table == null) return CompletableFuture.failedFuture(new IllegalStateException("Table cannot be null"));

        return CompletableFuture.supplyAsync(() -> {
            StringBuilder statement = new StringBuilder("INSERT INTO " + this.table + " (");

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
                return preparedStatement.getGeneratedKeys();
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while inserting into the table", e);
            }
        });
    }

    /**
     * Update the table where the columns match the values provided
     *
     * @return The result set from the update statement
     */
    public CompletableFuture<ResultSet> update() {
        if (this.table == null) return CompletableFuture.failedFuture(new IllegalStateException("Table cannot be null"));
        if (this.columns == null || this.columns.isEmpty()) return CompletableFuture.failedFuture(new IllegalStateException("Columns cannot be empty"));

        return CompletableFuture.supplyAsync(() -> {
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

                return preparedStatement.executeQuery();
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while updating the table", e);
            }
        });
    }

    /**
     * Delete from the table where the columns match the values provided
     *
     * @return The result set from the delete statement
     */
    public CompletableFuture<ResultSet> delete() {
        if (this.table == null) return CompletableFuture.failedFuture(new IllegalStateException("Table cannot be null"));
        if (this.columns.isEmpty()) return CompletableFuture.failedFuture(new IllegalStateException("Columns cannot be empty"));

        return CompletableFuture.supplyAsync(() -> {

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
                return preparedStatement.executeQuery();
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while deleting from the table", e);
            }
        });
    }

    /**
     * Drop the table from the database if it exists already
     *
     * @return If the table was dropped successfully
     */
    private CompletableFuture<Boolean> dropTable() {
        if (this.table == null) return CompletableFuture.failedFuture(new IllegalStateException("Table cannot be null"));

        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement preparedStatement = this.connection.prepareStatement("DROP TABLE IF EXISTS " + this.table)) {
                preparedStatement.executeUpdate();
                return true;
            } catch (Exception e) {
                EssentialsPlugin.get().getLogger().severe("An error occurred while dropping the table: " + e.getMessage());
                return false;
            }
        });
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
