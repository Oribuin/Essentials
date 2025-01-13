package dev.oribuin.essentials.api.database.serializer.impl.mutated;

import dev.oribuin.essentials.api.database.QueryResult;
import dev.oribuin.essentials.api.database.serializer.DataType;
import dev.oribuin.essentials.api.database.serializer.def.ColumnType;

import java.sql.PreparedStatement;
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
    public Map<DataType<K>, DataType<V>> deserialize(QueryResult.Row row, String name) {
        String result = row.getString(name);
        if (result == null) return null;

        return GSON.fromJson(result, MapDataType.class).map;
    }
    
}
