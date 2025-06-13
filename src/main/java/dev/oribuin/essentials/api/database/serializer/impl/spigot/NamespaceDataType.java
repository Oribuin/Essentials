package dev.oribuin.essentials.api.database.serializer.impl.spigot;

import dev.oribuin.essentials.api.database.QueryResult;
import dev.oribuin.essentials.api.database.serializer.DataType;
import dev.oribuin.essentials.api.database.serializer.def.ColumnType;
import org.bukkit.NamespacedKey;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NamespaceDataType extends DataType<NamespacedKey> {

    /**
     * Create a new DataType instance with a column type for the database
     */
    public NamespaceDataType() {
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
    public void serialize(PreparedStatement statement, int index, NamespacedKey value) throws SQLException {
        statement.setString(index, value.toString());
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
    public NamespacedKey deserialize(QueryResult.Row row, String name) {
        String result = row.getString(name);
        return result != null ? NamespacedKey.fromString(result) : null;
    }

}
