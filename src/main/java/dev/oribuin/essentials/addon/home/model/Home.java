package dev.oribuin.essentials.addon.home.model;

import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.serializer.def.DataTypes;
import dev.oribuin.essentials.util.StringPlaceholders;
import dev.oribuin.essentials.util.model.Placeholder;
import dev.oribuin.essentials.util.model.Placeholders;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.UUID;

public final class Home implements Placeholder {

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
     * @param row The row to construct a home from
     *
     * @return The home
     */
    public static Home construct(QueryResult.Row row) {
        String name = DataTypes.STRING.deserialize(row, "name");
        UUID owner = DataTypes.UUID.deserialize(row, "owner");
        Location location = DataTypes.LOCATION.deserialize(row, "location");
        if (name == null || owner == null || location == null) return null;

        return new Home(name, owner, location);
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
        if (block.getType().isAir()) return true;
        return location.getY() >= block.getY() + block.getBoundingBox().getHeight();
    }

    /**
     * The string placeholders for this object
     *
     * @return The compiled string placeholders
     */
    @Override
    public StringPlaceholders placeholders() {
        return Placeholders.builder()
                .add("homes", this.name)
                .add("location", String.format("%s, %s, %s @ %s", this.location.x(), this.location.y(), this.location.z(), this.location.getWorld().getName()))
                .add("owner-uuid", this.owner.toString()) // TODO: add %owner% = owner name
                .build();
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
