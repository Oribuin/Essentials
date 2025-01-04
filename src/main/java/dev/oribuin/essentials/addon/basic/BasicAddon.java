package dev.oribuin.essentials.addon.basic;

import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.addon.basic.command.EnderchestCommand;
import dev.oribuin.essentials.addon.basic.command.PingCommand;
import dev.oribuin.essentials.addon.basic.config.BasicConfig;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;

import java.util.List;

public class BasicAddon extends Addon {

    /**
     * Create a new instance of the addon
     *
     * @param plugin The plugin instance
     */
    public BasicAddon(EssentialsPlugin plugin) {
        super(plugin);
    }

    /**
     * The name of the addon
     * This will be used for logging and the name of the addon.
     */
    @Override
    public String name() {
        return "basic-commands";
    }

    /**
     * Get all the commands for the addon
     */
    @Override
    public List<BaseRoseCommand> commands() {
        return List.of(
                new EnderchestCommand(this.plugin),
                new PingCommand(this.plugin)
        );
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public List<AddonConfig> configs() {
        return List.of(
                new BasicConfig(),
                new BasicMessages()
        );
    }

}
