package dev.oribuin.essentials.api.database.serializer.impl.spigot;

import dev.oribuin.essentials.api.database.serializer.DataType;
import dev.oribuin.essentials.api.database.serializer.def.ColumnType;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemStackArrayDataType extends DataType<ItemStack[]> {

    /**
     * Create a new DataType instance with a column type for the database
     */
    public ItemStackArrayDataType() {
        super(ColumnType.BYTE_ARRAY);
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
    public void serialize(PreparedStatement statement, int index, ItemStack[] value) throws SQLException {
        statement.setBytes(index, ItemStack.serializeItemsAsBytes(value));
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
    public ItemStack[] deserialize(ResultSet resultSet, int index) throws SQLException {
        return ItemStack.deserializeItemsFromBytes(resultSet.getBytes(index));
    }

}
