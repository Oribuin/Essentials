package dev.oribuin.essentials.addon.warp.model;

import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.serializer.def.DataTypes;
import org.bukkit.Location;
import org.bukkit.permissions.Permissible;

public record Warp(String name, Location location) {

    /**
     * Check if a permissible can access the warp
     *
     * @param permissible The permissible to check
     *
     * @return true if they have permission to teleport
     */
    public boolean canAccess(Permissible permissible) {
        return permissible.hasPermission("essentials.warp. " + name.toLowerCase());
    }

    /**
     * Construct a warp from a result set row
     *
     * @param row The row to construct a warp from
     *
     * @return The warp
     */
    public static Warp construct(QueryResult.Row row) {
        String name = DataTypes.STRING.deserialize(row, "name");
        Location location = DataTypes.LOCATION.deserialize(row, "location");
        if (name == null || location == null) return null;

        return new Warp(name, location);
    }

}
