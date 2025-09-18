package dev.oribuin.essentials.addon.spawn;

import dev.oribuin.essentials.addon.Addon;
import dev.oribuin.essentials.addon.spawn.command.SetSpawnCommand;
import dev.oribuin.essentials.addon.spawn.command.SpawnCommand;
import dev.oribuin.essentials.addon.spawn.config.SpawnConfig;
import dev.oribuin.essentials.addon.spawn.config.SpawnMessages;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.util.model.Placeholders;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class SpawnAddon extends Addon {

    public static Permission SILENT_JOIN_PERMISSION = new Permission("essentials.join.silent", PermissionDefault.FALSE);
    public static Permission SILENT_LEAVE_PERMISSION = new Permission("essentials.leave.silent", PermissionDefault.FALSE);
    private static SpawnAddon instance;

    /**
     * Create a new instance of the addon
     */
    public SpawnAddon() {
        super("spawn");

        instance = this;
    }


    /**
     * Get all the commands for the addon
     */
    @Override
    public List<AddonCommand> getCommands() {
        return List.of(new SetSpawnCommand(this), new SpawnCommand(this));
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public Map<String, Supplier<AddonConfig>> getConfigs() {
        return Map.of("config", SpawnConfig::new, "messages", SpawnMessages::new);
    }

    /**
     * Get all the listeners for the addon
     */
    @Override
    public List<Listener> getListeners() {
        return List.of(this);
    }

    @Override
    public List<Permission> getPermissions() {
        return List.of(SILENT_JOIN_PERMISSION, SILENT_LEAVE_PERMISSION);
    }

    /**
     * Run first join stuff and any messages n shit :3
     *
     * @param event The join event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        SpawnConfig config = SpawnConfig.getInstance();
        SpawnMessages messages = SpawnMessages.getInstance();

        Player player = event.getPlayer();
        event.joinMessage(null);

        // Send the motd to the player after 1s
        if (config.useMotd()) this.getScheduler().runTaskLaterAsync(() -> messages.getMotd().send(player, player), 1, TimeUnit.SECONDS);

        // Run the first join stuff :3
        boolean isFirstJoin = !player.hasPlayedBefore();
        if (isFirstJoin) {
            if (config.isUseNewbieSpawn()) {
                player.teleport(config.getNewbieSpawnpoint().asLoc());
            }

            // Send the first join message to the server
            if (!player.hasPermission(SILENT_JOIN_PERMISSION)) {
                messages.getFirstJoinMessage().send(this.messageAudience(), player, Placeholders.of("total_players", Bukkit.getOfflinePlayers().length));
            }

            return;
        }

        // Send the join message to the server
        if (!player.hasPermission(SILENT_JOIN_PERMISSION)) {
            messages.getJoinMessage().send(this.messageAudience(), player, Placeholders.of("total_players", Bukkit.getOfflinePlayers().length));
        }

        // Teleport the player to spawn on login (if enabled)
        if (config.isAlwaysSpawnOnJoin()) {
            event.getPlayer().teleport(config.getSpawnpoint().asLoc());
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
            SpawnMessages.getInstance().getLeaveMessage().send(this.messageAudience(), event.getPlayer());
        }
    }

    /**
     * Send the player to the server spawn when they die
     *
     * @param event The respawn event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if (SpawnConfig.getInstance().isSpawnOnRespawn()) {
            event.setRespawnLocation(SpawnConfig.getInstance().getSpawnpoint().asLoc());
        }
    }

    private Audience messageAudience() {
        List<Audience> audiences = new ArrayList<>();
        audiences.add(Bukkit.getConsoleSender());
        audiences.addAll(Bukkit.getOnlinePlayers());
        return Audience.audience(audiences);
    }

    public static SpawnAddon getInstance() {
        return instance;
    }
}
