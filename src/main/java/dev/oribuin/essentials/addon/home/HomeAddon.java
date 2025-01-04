package dev.oribuin.essentials.addon.home;

import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.manager.DataManager;
import dev.oribuin.essentials.addon.home.command.HomeDeleteCommand;
import dev.oribuin.essentials.addon.home.command.HomeSetCommand;
import dev.oribuin.essentials.addon.home.command.HomeTPCommand;
import dev.oribuin.essentials.addon.home.config.HomeConfig;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.database.HomeRepository;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;

public class HomeAddon extends Addon implements Listener {

    private HomeRepository repository;

    /**
     * Create a new instance of the addon
     *
     * @param plugin The plugin instance
     */
    public HomeAddon(EssentialsPlugin plugin) {
        super(plugin);
    }

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
        this.repository = DataManager.create(HomeRepository.class);

        if (this.repository == null) {
            this.logger.severe("The HomeRepository is null, this plugin will not work correctly.");
            EssentialsPlugin.unload(this);
            return;
        }

        this.repository.establishTables();
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
                new HomeSetCommand(this.plugin),
                new HomeTPCommand(this.plugin)
        );
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public List<AddonConfig> configs() {
        return List.of(new HomeConfig(), new HomeMessages());
    }

    /**
     * Get the repository for the addon
     */
    public HomeRepository repository() {
        return repository;
    }

    /**
     * Get the maximum amount of homes a player can have
     *
     * @param player The player to get the limit of
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
