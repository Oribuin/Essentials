package dev.oribuin.essentials.database.serializer.impl.spigot;

import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.serializer.DataType;
import dev.oribuin.essentials.database.serializer.def.ColumnType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LocationDataType extends DataType<Location> {

    /**
     * Create a new DataType instance with a column type for the database
     */
    public LocationDataType() {
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
    public void serialize(PreparedStatement statement, int index, Location value) throws SQLException {
        World world = value.getWorld();
        statement.setString(index, String.format("%s;%s;%s;%s;%s;%s",
                world == null ? "null" : world.getName(),
                value.getX(),
                value.getY(),
                value.getZ(),
                value.getYaw(),
                value.getPitch()
        ));
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
    public Location deserialize(QueryResult.Row row, String name) {
        String result = row.getString(name);
        if (result == null) return null;

        String[] data = result.split(";");
        if (data.length < 4) return null;

        World world = Bukkit.getWorld(data[0]);
        if (world == null) return null;

        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        float yaw = 0f;
        float pitch = 0f;

        // parse yaw & pitch if applicable
        if (data.length <= 5) yaw = Float.parseFloat(data[4]);
        if (data.length <= 6) pitch = Float.parseFloat(data[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

}
