package dev.oribuin.essentials.addon.home;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.home.command.HomeDeleteCommand;
import dev.oribuin.essentials.addon.home.command.HomeSetCommand;
import dev.oribuin.essentials.addon.home.command.HomeTPCommand;
import dev.oribuin.essentials.addon.home.config.HomeConfig;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.database.HomeRepository;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.AddonMessages;
import dev.oribuin.essentials.manager.DataManager;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;

public class HomeAddon extends Addon {

    private HomeRepository repository;
    private AddonMessages messages;

    /**
     * The name of the addon
     * This will be used for logging and the name of the addon.
     */
    @Override
    public String name() {
        return "homes";
    }

    /**
     * When the addon is finished loading and is ready to be used.
     */
    @Override
    public void enable() {
        this.messages = new AddonMessages(this.folder.toPath(), "messages.yml");
        this.repository = DataManager.create(HomeRepository.class);

        if (this.repository == null) {
            this.logger.severe("The HomeRepository is null, this plugin will not work correctly.");
            AddonProvider.unload(this);
            return;
        }
        
        this.messages.register(this.plugin);

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
    public List<BaseRoseCommand> commands() {
        return List.of(
                new HomeDeleteCommand(this.plugin),
                new HomeSetCommand(this.plugin, this),
                new HomeTPCommand(this.plugin)
        );
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public List<AddonConfig> configs() {
        return List.of(new HomeConfig());
    }

    /**
     * Get all the listeners for the addon
     */
    @Override
    public List<Listener> listeners() {
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

    public AddonMessages messages() {
        return messages;
    }

    /**
     * Get the maximum amount of homes a player can have
     *
     * @param player The player to get the limit of
     *
     * @return The limit of homes the player can have
     */
    public static int limit(Player player) {
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
