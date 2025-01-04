package dev.oribuin.essentials.api.database.serializer.impl.mutated;

import dev.oribuin.essentials.api.database.serializer.DataType;
import dev.oribuin.essentials.api.database.serializer.def.ColumnType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MapDataType<K, V> extends DataType<Map<DataType<K>, DataType<V>>> {

    private final DataType<K> keyType;
    private final DataType<V> valueType;
    private final Map<DataType<K>, DataType<V>> map;

    public MapDataType(DataType<K> keyType, DataType<V> valueType, Map<DataType<K>, DataType<V>> map) {
        super(ColumnType.TEXT);
        this.keyType = keyType;
        this.valueType = valueType;
        this.map = map;
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
    public void serialize(PreparedStatement statement, int index, Map<DataType<K>, DataType<V>> value) throws SQLException {
        statement.setString(index, GSON.toJson(new MapDataType<>(this.keyType, this.valueType, this.map)));
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
    public Map<DataType<K>, DataType<V>> deserialize(ResultSet resultSet, int index) throws SQLException {
        return GSON.fromJson(resultSet.getString(index), MapDataType.class).map; // could not work
    }
}