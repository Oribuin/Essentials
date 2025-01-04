package dev.oribuin.essentials.api.database.serializer.impl;

import dev.oribuin.essentials.api.database.serializer.DataType;
import dev.oribuin.essentials.api.database.serializer.def.ColumnType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanDataType extends DataType<Boolean> {

    /**
     * Create a new DataType instance with a column type for the database
     */
    public BooleanDataType() {
        super(ColumnType.BOOLEAN);
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
    public void serialize(PreparedStatement statement, int index, Boolean value) throws SQLException {
        statement.setBoolean(index, value);
    }

    /**
     * Deserialize a value from a result set
     *
     * @param resultSet The result set
     * @param index     The index
     * @throws SQLException If an error occurs while deserializing the value
     */
    @Override
    public Boolean deserialize(ResultSet resultSet, int index) throws SQLException {
        return resultSet.getBoolean(index);
    }

}
