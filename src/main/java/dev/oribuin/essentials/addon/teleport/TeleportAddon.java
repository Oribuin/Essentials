package dev.oribuin.essentials.addon.teleport;

import dev.oribuin.essentials.addon.teleport.command.BackCommand;
import dev.oribuin.essentials.addon.teleport.command.TpAcceptCommand;
import dev.oribuin.essentials.addon.teleport.command.TpAskCommand;
import dev.oribuin.essentials.addon.teleport.command.TpAskHereCommand;
import dev.oribuin.essentials.addon.teleport.command.TpDenyCommand;
import dev.oribuin.essentials.addon.teleport.config.TeleportConfig;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.oribuin.essentials.addon.teleport.model.TeleportRequest;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeleportAddon extends Addon {

    private final List<TeleportRequest> requests = new ArrayList<>();
    private final Map<String, Permission> worldPerms = new HashMap<>();
    private final Map<UUID, Location> previousLocations = new HashMap<>();

    /**
     * When the addon is finished loading and is ready to be used.
     */
    @Override
    public void enable() {
        Bukkit.getWorlds().forEach(world -> {
            String perm = "essentials.teleport." + world.getName().toLowerCase();
            Permission permission = new Permission(perm,
                    "Checks if a person is allowed to teleport to the world using /tp/back/etc",
                    PermissionDefault.TRUE
            );

            PluginManager manager = this.plugin.getServer().getPluginManager();
            Permission loaded = manager.getPermission(perm);
            if (loaded == null) {
                manager.addPermission(permission);
            }

            this.worldPerms.put(world.getName(), loaded != null ? loaded : permission);
        });
    }

    /**
     * Get the permission for an associated world
     *
     * @param worldName The world name
     *
     * @return The
     */
    public @NotNull String getPerm(@NotNull String worldName) {
        Permission perm = this.worldPerms.get(worldName.toLowerCase());
        return perm != null ? perm.getName() : "essentials.teleport." + worldName.toLowerCase();
    }

    /**
     * Get a sender's outgoing teleport request
     *
     * @param sender The person who sent the request
     *
     * @return The teleport request if available
     */
    public @Nullable TeleportRequest getOutgoing(@NotNull UUID sender) {
        return this.requests.stream()
                .sorted(Comparator.comparingLong(TeleportRequest::when).reversed())
                .filter(x -> x.isSender(sender) && !x.hasExpired())
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a target's incoming teleport request
     *
     * @param target The person who received the request
     *
     * @return The teleport request if available
     */
    public @Nullable TeleportRequest getIncoming(@NotNull UUID target) {
        return this.requests.stream()
                .sorted(Comparator.comparingLong(TeleportRequest::when).reversed())
                .filter(x -> x.isTarget(target) && !x.hasExpired())
                .findFirst()
                .orElse(null);
    }

    /**
     * Cleanup any hanging data left over when the user logs out
     *
     * @param event The player quit event
     */
    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Remove previous location from the target when log out
        this.previousLocations.remove(player.getUniqueId());

        // Remove any active requests
        this.requests.removeIf(x -> x.isSender(player.getUniqueId()) || x.isTarget(player.getUniqueId()));
    }

    /**
     * Store the player's previous teleport location in /back
     *
     * @param event The player teleport event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent event) {
        // Check if the player has access to teleport to the world
        if (!event.getPlayer().hasPermission(this.getPerm(event.getTo().getWorld().getName()))) {
            TeleportMessages.DISABLED_WORLD.send(event.getPlayer());
            event.setCancelled(true);
            return;
        }

        // Mark it as a previous location if successful
        this.previousLocations.put(event.getPlayer().getUniqueId(), event.getFrom());
    }

    /**
     * Store the players death location in /back
     *
     * @param event The death event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        this.previousLocations.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
    }

    /**
     * The name of the addon
     * This will be used for logging and the name of the addon.
     */
    @Override
    public String name() {
        return "teleport";
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public List<AddonConfig> configs() {
        return List.of(new TeleportConfig(), new TeleportMessages());
    }

    /**
     * Get all the listeners for the addon
     */
    @Override
    public List<Listener> listeners() {
        return List.of(this);
    }

    /**
     * Get all the commands for the addon
     */
    @Override
    public List<BaseRoseCommand> commands() {
        return List.of(
                new BackCommand(this.plugin),
                new TpAcceptCommand(this.plugin),
                new TpAskCommand(this.plugin),
                new TpAskHereCommand(this.plugin),
                new TpDenyCommand(this.plugin)
        );
    }

    public List<TeleportRequest> requests() {
        return requests;
    }

    public Map<UUID, Location> previousLocations() {
        return previousLocations;
    }
}
