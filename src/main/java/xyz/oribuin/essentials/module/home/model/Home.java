package xyz.oribuin.essentials.module.home.model;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.essentials.Essentials;
import xyz.oribuin.essentials.module.home.HomeModule;
import xyz.oribuin.essentials.module.home.config.HomeConfig;
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

        if (cost > 0.0) {
            player.sendMessage("You have been charged $" + cost + " to teleport to your home.");
        }

        // Create the tp effects task
        BukkitTask effectTask;
        if (config.get(HomeConfig.TP_EFFECTS).asBoolean() && teleportDelay > 0) {
            // Give the player blindness
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                    teleportDelay * 20 - 10, 4,
                    false,
                    false,
                    false)
            );

            effectTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Essentials.get(), () -> player.spawnParticle(
                    Particle.SPELL_WITCH,
                    player.getLocation(),
                    10,
                    0.5, 0.5, 0.5,
                    0.1
            ), 0L, 20L);
        } else {
            effectTask = null;
        }

        // Teleport the player to the location
        Bukkit.getScheduler().runTaskLater(Essentials.get(), () -> {
            if (effectTask != null)
                effectTask.cancel();

            PaperLib.teleportAsync(player, this.location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }, teleportDelay * 20L);

    }

}
