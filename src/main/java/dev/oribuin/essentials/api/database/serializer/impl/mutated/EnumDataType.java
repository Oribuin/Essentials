package dev.oribuin.essentials.api.database.serializer.impl.mutated;

import dev.oribuin.essentials.api.database.serializer.DataType;
import dev.oribuin.essentials.api.database.serializer.def.ColumnType;
import dev.oribuin.essentials.util.EssUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
     * Deserialize a value from a result set
     *
     * @param resultSet The result set
     * @param index     The index
     *
     * @throws SQLException If an error occurs while deserializing the value
     */
    @Override
    public T deserialize(ResultSet resultSet, int index) throws SQLException {
        return EssUtils.getEnum(this.type, resultSet.getString(index), null);
    }

}
