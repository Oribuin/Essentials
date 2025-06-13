package dev.oribuin.essentials.addon.teleport;

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
     * Get all the commands for the addon
     */
    @Override
    public List<BaseRoseCommand> commands() {
        return List.of(
                new TpAcceptCommand(this.plugin),
                new TpAskCommand(this.plugin),
                new TpAskHereCommand(this.plugin),
                new TpDenyCommand(this.plugin)
        );
    }

    public List<TeleportRequest> getRequests() {
        return requests;
    }
}
