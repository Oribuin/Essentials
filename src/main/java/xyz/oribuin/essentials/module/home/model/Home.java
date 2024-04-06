package xyz.oribuin.essentials.module.home.model;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.UUID;

public record Home(String name, UUID owner, Location location) {

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

    /**
     * Get the maximum amount of homes a player can have
     *
     * @param player The player to get the limit of
     * @return The limit of homes the player can have
     */
    public static int getLimit(Player player) {
        int amount = -1;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            final String target = info.getPermission().toLowerCase();

            if (!(target.startsWith("essentials.home.limit.") && info.getValue())) continue;

            try {
                int value = Integer.parseInt(target.replace("essentials.home.limit.", ""));
                if (value > amount) amount = value;
            } catch (NumberFormatException ignored) {
                break;
            }
        }

        return amount;
    }

}
