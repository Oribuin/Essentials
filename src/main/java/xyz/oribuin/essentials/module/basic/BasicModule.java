package xyz.oribuin.essentials.module.basic;

import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import xyz.oribuin.essentials.Essentials;
import xyz.oribuin.essentials.api.Module;
import xyz.oribuin.essentials.api.config.ModuleConfig;
import xyz.oribuin.essentials.module.basic.command.EnderchestCommand;
import xyz.oribuin.essentials.module.basic.command.PingCommand;
import xyz.oribuin.essentials.module.basic.config.BasicConfig;
import xyz.oribuin.essentials.module.basic.config.BasicMessages;

import java.util.List;

public class BasicModule extends Module {

    /**
     * Create a new instance of the module
     *
     * @param plugin The plugin instance
     */
    public BasicModule(Essentials plugin) {
        super(plugin);
    }

    /**
     * The name of the module
     * This will be used for logging and the name of the module.
     */
    @Override
    public String name() {
        return "basic-commands";
    }

    /**
     * Get all the commands for the module
     */
    @Override
    public List<BaseRoseCommand> commands() {
        return List.of(
                new EnderchestCommand(this.plugin),
                new PingCommand(this.plugin)
        );
    }

    /**
     * Get all the configuration files for the module
     */
    @Override
    public List<ModuleConfig> configs() {
        return List.of(
                new BasicConfig(),
                new BasicMessages()
        );
    }

}
