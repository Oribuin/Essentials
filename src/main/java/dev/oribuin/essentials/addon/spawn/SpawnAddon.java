package dev.oribuin.essentials.addon.spawn;

import dev.oribuin.essentials.addon.spawn.command.SetSpawnCommand;
import dev.oribuin.essentials.addon.spawn.command.SpawnCommand;
import dev.oribuin.essentials.addon.spawn.config.SpawnConfig;
import dev.oribuin.essentials.addon.spawn.config.SpawnMessages;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.util.Placeholders;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

public class SpawnAddon extends Addon {

    public static Permission SILENT_JOIN_PERMISSION = new Permission("essentials.join.silent", PermissionDefault.FALSE);
    public static Permission SILENT_LEAVE_PERMISSION = new Permission("essentials.leave.silent", PermissionDefault.FALSE);
    private final SpawnConfig config = new SpawnConfig();

    /**
     * The name of the addon
     * This will be used for logging and the name of the addon.
     */
    @Override
    public String name() {
        return "spawn";
    }

    /**
     * Get all the commands for the addon
     */
    @Override
    public List<BaseRoseCommand> commands() {
        return List.of(new SetSpawnCommand(this.plugin), new SpawnCommand(this.plugin));
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public List<AddonConfig> configs() {
        return List.of(this.config, new SpawnMessages());
    }

    /**
     * Get all the listeners for the addon
     */
    @Override
    public List<Listener> listeners() {
        return List.of(this);
    }

    @Override
    public List<Permission> permissions() {
        return List.of(SILENT_JOIN_PERMISSION, SILENT_LEAVE_PERMISSION);
    }

    /**
     * Run first join stuff and any messages n shit :3
     *
     * @param event The join event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(null);

        // Send the motd to the player after 1s
        if (SpawnConfig.USE_MOTD.value()) {
            SpawnMessages.MOTD.value().send(player, player);
        }

        // Run the first join stuff :3
        boolean isFirstJoin = !player.hasPlayedBefore();
        if (isFirstJoin) {
            if (SpawnConfig.USE_NEWBIE_SPAWN.value()) {
                player.teleport(SpawnConfig.NEWBIE_SPAWN.value().asLoc());
            }

            // Send the first join message to the server
            if (!player.hasPermission(SILENT_JOIN_PERMISSION)) {
                SpawnMessages.FIRST_JOIN_MESSAGE.send(this.messageAudience(), player, Placeholders.of(
                        "total_players", Bukkit.getOfflinePlayers().length
                ));
            }

            return;
        }

        // Send the join message to the server
        if (!player.hasPermission(SILENT_JOIN_PERMISSION)) {
            SpawnMessages.JOIN_MESSAGE.send(this.messageAudience(), player, Placeholders.of(
                    "total_players", Bukkit.getOfflinePlayers().length
            ));
        }

        // Teleport the player to spawn on login (if enabled)
        if (SpawnConfig.ALWAYS_SPAWN_ON_JOIN.value()) {
            event.getPlayer().teleport(SpawnConfig.SPAWNPOINT.value().asLoc());
        }
    }

    /**
     * Send the leave messages when a player leaves the server
     *
     * @param event The leave event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
        if (!event.getPlayer().hasPermission(SILENT_LEAVE_PERMISSION)) {
            SpawnMessages.LEAVE_MESSAGE.send(this.messageAudience(), event.getPlayer());
        }
    }

    /**
     * Send the player to the server spawn when they die
     *
     * @param event The respawn event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if (SpawnConfig.SPAWN_ON_RESPAWN.value()) {
            event.setRespawnLocation(SpawnConfig.SPAWNPOINT.value().asLoc());
        }
    }

    private Audience messageAudience() {
        List<Audience> audiences = new ArrayList<>();
        audiences.add(Bukkit.getConsoleSender());
        audiences.addAll(Bukkit.getOnlinePlayers());
        return Audience.audience(audiences);
    }

    public SpawnConfig config() {
        return config;
    }
}
