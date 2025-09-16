package dev.oribuin.essentials.database.serializer.impl;

import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.serializer.DataType;
import dev.oribuin.essentials.database.serializer.def.ColumnType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringDataType extends DataType<String> {

    /**
     * Create a new DataType instance with a column type for the database
     */
    public StringDataType() {
        super(ColumnType.TEXT);
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
    public void serialize(PreparedStatement statement, int index, String value) throws SQLException {
        statement.setString(index, value);
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
    public String deserialize(QueryResult.Row row, String name) {
        return row.get(String.class, name);
    }

}

