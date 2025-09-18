package dev.oribuin.essentials.addon.home;

import dev.oribuin.essentials.addon.Addon;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.home.command.HomeDeleteCommand;
import dev.oribuin.essentials.addon.home.command.HomeSetCommand;
import dev.oribuin.essentials.addon.home.command.HomeTPCommand;
import dev.oribuin.essentials.addon.home.config.HomeConfig;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.database.HomeRepository;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.manager.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class HomeAddon extends Addon {

    private HomeRepository repository;
    private static HomeAddon instance;

    /**
     * Create a new instance of the addon
     */
    public HomeAddon() {
        super("homes");

        instance = this;
    }

    /**
     * Before the addon starts loading, register whatever is needed to be used when its enabled
     */
    @Override
    public void load() {
        // Register Command Argument Providers
        //        this.registerParser(Home.class, new HomeArgumentHandler());
    }

    /**
     * When the addon is finished loading and is ready to be used.
     */
    @Override
    public void enable() {
        this.repository = DataManager.create(HomeRepository.class);

        if (this.repository == null) {
            this.logger.severe("The HomeRepository is null, this plugin will not work correctly.");
            AddonProvider.unload(this);
            return;
        }

        // Load Existing Users
        Bukkit.getOnlinePlayers().forEach(player -> this.repository.load(player.getUniqueId()));
    }


    /**
     * When the addon is being disabled.
     */
    @Override
    public void disable() {
        if (this.repository != null) {
            this.repository.all().clear();
        }
    }

    /**
     * Get all the commands for the addon
     */
    @Override
    public List<AddonCommand> getCommands() {
        return List.of(
                new HomeDeleteCommand(this),
                new HomeSetCommand(this),
                new HomeTPCommand()
        );
    }


    /**
     * Get all the configuration files for the addon
     */
    @Override
    public Map<String, Supplier<AddonConfig>> getConfigs() {
        return Map.of(
                "config", HomeConfig::new,
                "messages", HomeMessages::new
        );
    }

    /**
     * Get all the listeners for the addon
     */
    @Override
    public List<Listener> getListeners() {
        return List.of(this);
    }

    /**
     * Load all the homes for a player from the database
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        this.repository.load(event.getPlayer().getUniqueId());
    }

    /**
     * Remove all the homes for a player from the cache
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.repository.unload(event.getPlayer().getUniqueId());
    }

    /**
     * Get the repository for the addon
     */
    public HomeRepository repository() {
        return repository;
    }

    public static HomeAddon getInstance() {
        return instance;
    }

    /**
     * Get the maximum amount of homes a player can have
     *
     * @param player The player to get the limit of
     *
     * @return The limit of homes the player can have
     */
    public static int limit(Player player) {
        int amount = 0;
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
