package dev.oribuin.essentials.api.database.serializer.impl.mutated;

import com.google.gson.Gson;
import dev.oribuin.essentials.api.database.serializer.DataType;
import dev.oribuin.essentials.api.database.serializer.def.ColumnType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ListDataType<T> extends DataType<T> {

    private static final Gson GSON = new Gson();
    private final Class<T> type;
    private final List<T> list;

    public ListDataType(Class<T> type, List<T> list) {
        super(ColumnType.STRING);
        this.type = type;
        this.list = list;
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
        statement.setString(index, GSON.toJson(new ListDataType<>(this.type, this.list)));
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
        return GSON.fromJson(resultSet.getString(index), this.type);
    }

}
