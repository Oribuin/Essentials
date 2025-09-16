package dev.oribuin.essentials.database.serializer.impl.mutated;

import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.serializer.DataType;
import dev.oribuin.essentials.database.serializer.def.ColumnType;
import dev.oribuin.essentials.util.EssUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EnumDataType<T extends Enum<T>> extends DataType<T> {

    private final Class<T> type;

    /**
     * Create a new DataType instance with a column type for the database
     *
     * @param type The type of the enum
     */
    public EnumDataType(Class<T> type) {
        super(ColumnType.TEXT);
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
    @Override
    public void serialize(PreparedStatement statement, int index, T value) throws SQLException {
        statement.setString(index, value.name());
    }

    /**
     * Deserialize a value from a column row
     *
     * @param row  The row to get the value from
     * @param name The name of the value
     *
     * @return The deserialized value
     *
     * @throws SQLException If an error occurs while deserializing the value
     */
    @Override
    public T deserialize(QueryResult.Row row, String name) {
        return EssUtils.getEnum(this.type, row.getString(name), null);
    }

}
