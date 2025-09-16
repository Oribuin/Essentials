package dev.oribuin.essentials.database.serializer;

import com.google.gson.Gson;
import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.serializer.def.ColumnType;

import java.sql.PreparedStatement;
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
     * Deserialize a value from a column row
     *
     * @param row  The row to get the value from
     * @param name The name of the value
     *
     * @return The deserialized value
     */
    public abstract T deserialize(QueryResult.Row row, String name);

    /**
     * The mysql column type for the database
     *
     * @return The column type
     */
    public String columnType() {
        return this.type;
    }
}
