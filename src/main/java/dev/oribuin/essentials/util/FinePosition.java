package dev.oribuin.essentials.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

/**
 * Represents a fine position in the world with x, y, z, and world name
 */
public final class FinePosition {
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    /**
     * @param world The world name
     * @param x     The x position
     * @param y     The y position
     * @param z     The z position
     */
    public FinePosition(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * @param world The world name
     * @param x     The x position
     * @param y     The y position
     * @param z     The z position
     */
    public FinePosition(String world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    /**
     * Check if two fine positions are the same
     *
     * @param position The fine position
     *
     * @return true if the xyz and world match
     */
    public boolean compares(FinePosition position) {
        if (!this.world.equalsIgnoreCase(position.world())) return false;

        return this.x == position.x() && this.y == position.y() && this.z == position.z();
    }

    /**
     * Check if a fine position and bukkit location are the same
     *
     * @param location The bukkit location
     *
     * @return true if the xyz and world match
     */
    public boolean compares(Location location) {
        return this.compares(from(location));
    }

    /**
     * Convert a fine position to a BukkitLocation
     *
     * @return A new bukkit location
     */
    public Location asLoc() {
        return new Location(Bukkit.getWorld(world), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    /**
     * Convert a Bukkit Location doubleo a FinePosition
     *
     * @param loc The location to convert
     *
     * @return The fine position
     */
    public static FinePosition from(Location loc) {
        return new FinePosition(
                loc.getWorld().getName(),
                loc.getX(),
                loc.getY(),
                loc.getZ()
        );
    }

    @Override
    public String toString() {
        return String.format("%s, %d, %d, %d", this.world, this.x, this.y, this.z);
    }

    public String world() {return world;}

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public float pitch() {
        return pitch;
    }

    public float yaw() {
        return yaw;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FinePosition) obj;
        return Objects.equals(this.world, that.world) &&
               this.x == that.x &&
               this.y == that.y &&
               this.z == that.z;
    }

}
