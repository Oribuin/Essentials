package dev.oribuin.essentials.api.database.serializer;

import com.google.gson.Gson;
import dev.oribuin.essentials.api.database.serializer.def.ColumnType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DataType<T> {

    protected final String type;
    protected final Gson GSON = new Gson();

    /**
     * Create a new DataType instance with a column type for the database
     *
     * @param type The column type
     */
    public DataType(ColumnType type) {
        this.type = type.realValue();
    }

    /**
     * Create a new DataType instance with a column type for the database
     *
     * @param type The column type
     */
    public DataType(String type) {
        this.type = type;
    }

    /**
     * Serialize a value to a prepared statement for a specific index
     *
     * @param statement The prepared statement
     * @param index     The index to serialize the value to
     * @param value     The value to serialize
     *
     * @throws SQLException If an error occurs while serializing the value
     */
    public abstract void serialize(PreparedStatement statement, int index, T value) throws SQLException;

    /**
     * Deserialize a value from a result set
     *
     * @param resultSet The result set
     * @param index     The index
     *
     * @throws SQLException If an error occurs while deserializing the value
     */
    public abstract T deserialize(ResultSet resultSet, int index) throws SQLException;

    /**
     * Deserialize a value from a result set
     *
     * @param resultSet The result set
     * @param column     The column
     *
     * @throws SQLException If an error occurs while deserializing the value
     */
    public final T deserialize(ResultSet resultSet, String column) throws SQLException {
        return this.deserialize(resultSet, resultSet.findColumn(column));
    }

    /**
     * The mysql column type for the database
     *
     * @return The column type
     */
    public String columnType() {
        return this.type;
    }
}
