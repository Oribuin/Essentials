package xyz.oribuin.essentials.module.home.model;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.oribuin.essentials.Essentials;
import xyz.oribuin.essentials.module.home.config.HomeConfig;
import xyz.oribuin.essentials.module.home.HomeModule;
import xyz.oribuin.essentials.module.home.config.HomeMessages;

import java.util.List;
import java.util.UUID;

public record Home(String name, UUID owner, Location location) {

    /**
     * Teleport a player to the home location
     *
     * @param player The player to teleport
     */
    public void teleport(Player player) {
        HomeModule module = Essentials.getModule(HomeModule.class);
        if (module == null || !module.isEnabled()) return;

        HomeConfig config = module.config(HomeConfig.class);
        HomeMessages messages = module.config(HomeMessages.class);
        if (config == null || messages == null) return;

        // Check if the world is disabled
        List<String> disabledWorlds = config.get(HomeConfig.DISABLED_WORLDS.getPath()).asStringList();
        if (disabledWorlds.contains(this.location.getWorld().getName())) {
            messages.send(player, HomeMessages.DISABLED_WORLD.getPath());
            return;
        }

        int cooldown = config.get(HomeConfig.TP_COOLDOWN.getPath()).asInt();
        int teleportDelay = config.get(HomeConfig.TP_DELAY.getPath()).asInt();
        double cost = config.get(HomeConfig.TP_COST.getPath()).asDouble();

        // TODO: Check for teleport delay
        // TODO: Check for cooldown between commands
        // TODO: Check for teleport cost

        if (teleportDelay <= 0) {
            PaperLib.teleportAsync(player, this.location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            return;
        }

        Bukkit.getScheduler().runTaskLater(Essentials.get(), () -> {

        });
    }

}
