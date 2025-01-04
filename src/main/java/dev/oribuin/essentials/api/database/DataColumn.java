package dev.oribuin.essentials.api.database;

import dev.oribuin.essentials.api.database.serializer.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataColumn<T> {

    private final String name;
    private final DataType<T> type;
    private T value;
    private boolean nullable;

    /**
     * Create a new DataColumn instance with a name, type, and value, not to be used directly ( because DataColumn.of() is prettier :3 )
     *
     * @param name  The name of the column
     * @param type  The type of the column
     * @param value The value of the column
     */
    private DataColumn(String name, DataType<T> type, T value) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.nullable = true;
    }

    /**
     * Create a new DataColumn instance with a name, type, and value to use
     *
     * @param name  The name of the column
     * @param type  The type of the column
     * @param value The value of the column
     * @param <T>   The type of the column
     *
     * @return The new DataColumn instance
     */
    public static <T> DataColumn<T> of(String name, DataType<T> type, T value) {
        return new DataColumn<>(name, type, value);
    }

    /**
     * Serialize a value to a prepared statement for a specific index
     *
     * @param statement The prepared statement
     * @param index     The index to serialize the value to
     *
     * @throws SQLException If an error occurs while serializing the value
     */
    public void serialize(PreparedStatement statement, int index) throws SQLException {
        this.type.serialize(statement, index, this.value);
    }

    /**
     * Construct the column for the database
     *
     * @return The constructed column
     */
    public String construct() {
        return String.format("%s %s %s",
                this.name,
                this.type.type(),
                this.nullable ? "NULL" : "NOT NULL"
        );
    }

    /**
     * @return The name of the column
     */
    public String name() {
        return name;
    }

    /**
     * @return The type of the column
     */
    public DataType<T> dataType() {
        return type;
    }

    /**
     * @return The value of the column
     */
    public T value() {
        return value;
    }

    /**
     * Set the value of the column
     *
     * @param value The value to set
     */
    public void value(T value) {
        this.value = value;
    }

    /**
     * @return If the column is nullable
     */
    public boolean nullable() {
        return nullable;
    }

    /**
     * Set if the column is nullable
     *
     * @param nullable If the column is nullable
     */
    public void nullable(boolean nullable) {
        this.nullable = nullable;
    }

}
