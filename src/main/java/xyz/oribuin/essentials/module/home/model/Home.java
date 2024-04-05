package xyz.oribuin.essentials.module.home.model;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public record Home(String name, UUID owner, Location location) {

    /**
     * Teleport a player to the home location
     *
     * @param player The player to teleport
     * @return If the player was successfully teleported
     */
    public boolean teleport(Player player) {
        // TODO: Paper async teleport
        return player.teleport(this.location);
    }

}
