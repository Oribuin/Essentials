package dev.oribuin.essentials.addon.basic;

import dev.oribuin.essentials.addon.basic.command.EnderchestCommand;
import dev.oribuin.essentials.addon.basic.command.FlyCommand;
import dev.oribuin.essentials.addon.basic.command.PingCommand;
import dev.oribuin.essentials.addon.basic.command.gamemode.GamemodeCommand;
import dev.oribuin.essentials.addon.basic.config.BasicConfig;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;

import java.util.List;

public class BasicAddon extends Addon {

    /**
     * The name of the addon
     * This will be used for logging and the name of the addon.
     */
    @Override
    public String name() {
        return "basics";
    }

    /**
     * Get all the commands for the addon
     */
    @Override
    public List<BaseRoseCommand> commands() {
        return List.of(
                new EnderchestCommand(this.plugin),
                new PingCommand(this.plugin),
                new FlyCommand(this.plugin),

                // Gamemode Commands
                new GamemodeCommand(this.plugin),
                new GamemodeCommand.AdventureCommand(this.plugin),
                new GamemodeCommand.CreativeCommand(this.plugin),
                new GamemodeCommand.SpectatorCommand(this.plugin),
                new GamemodeCommand.CreativeCommand(this.plugin)
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
