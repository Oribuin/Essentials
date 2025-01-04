package dev.oribuin.essentials.api.database.serializer.impl.spigot;

import dev.oribuin.essentials.api.database.serializer.def.ColumnType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import dev.oribuin.essentials.api.database.serializer.DataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        statement.setString(index, String.format("%s;%s;%s;%s;%s,%s",
                world == null ? "null" : world.getName(),
                value.getX(),
                value.getY(),
                value.getZ(),
                value.getYaw(),
                value.getPitch()
        ));
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
    public Location deserialize(ResultSet resultSet, int index) throws SQLException {
        String[] data = resultSet.getString(index).split(";");
        if (data.length != 6) return null;

        World world = Bukkit.getWorld(data[0]);
        if (world == null) return null;

        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        float yaw = Float.parseFloat(data[4]);
        float pitch = Float.parseFloat(data[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

}
