package dev.oribuin.essentials.addon.home.model;

import dev.oribuin.essentials.api.database.serializer.def.DataTypes;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class Home {

    private final String name;
    private final UUID owner;
    private Location location;

    public Home(String name, UUID owner, Location location) {
        this.name = name;
        this.owner = owner;
        this.location = location;
    }

    /**
     * Construct a home from a result set row
     *
     * @param resultSet The result set
     *
     * @return The home
     *
     * @throws SQLException If an error occurs
     */
    public static Home construct(ResultSet resultSet) throws SQLException {
        return new Home(
                DataTypes.STRING.deserialize(resultSet, "name"),
                DataTypes.UUID.deserialize(resultSet, "owner"),
                DataTypes.LOCATION.deserialize(resultSet, "location")
        );
    }

    /**
     * Check if this current home is safe to teleport to
     *
     * @return If the home is safe
     */
    public boolean isSafe() {
        return isSafe(this.location);
    }

    /**
     * Check if a location is safe to teleport to
     *
     * @param location The location to check
     *
     * @return If the location is safe
     */
    public static boolean isSafe(Location location) {
        if (location == null) return false;

        Block block = location.getBlock();
        Block below = block.getRelative(0, -1, 0);

        // don't allow homes in the air or in liquids
        if (below.getType().isAir() || below.isLiquid()) return false;

        // Make sure the player is not in a block
        return !(location.getY() <= location.getBlockY() + block.getBoundingBox().getHeight());
    }

    public String name() {
        return name;
    }

    public UUID owner() {
        return owner;
    }

    public Location location() {
        return location;
    }

    public void location(Location location) {
        this.location = location;
    }


    @Override
    public String toString() {
        return "Home[" +
               "name=" + name + ", " +
               "owner=" + owner + ", " +
               "location=" + location + ']';
    }


}
