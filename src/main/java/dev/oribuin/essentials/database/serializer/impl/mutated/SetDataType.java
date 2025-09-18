package dev.oribuin.essentials.database.serializer.impl.mutated;

import com.google.gson.Gson;
import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.serializer.DataType;
import dev.oribuin.essentials.database.serializer.def.ColumnType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

@SuppressWarnings("FieldMayBeFinal")
public class SetDataType<T> extends DataType<Set<T>> {

    private static final Gson GSON = new Gson();
    private transient final DataType<T> type;
    private Set<T> values;

    public SetDataType(DataType<T> type, Set<T> values) {
        super(ColumnType.TEXT);
        this.type = type;
        this.values = values;
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
    public void serialize(PreparedStatement statement, int index, Set<T> value) throws SQLException {
        statement.setString(index, GSON.toJson(new SetDataType<>(this.type, value)));
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
    public Set<T> deserialize(QueryResult.Row row, String name) {
        String result = row.getString(name);
        if (result == null) return null;

        SetDataType<T> dataType = GSON.fromJson(result, SetDataType.class);
        return dataType.getValues();
    }

    public Set<T> getValues() {
        return values;
    }
}
