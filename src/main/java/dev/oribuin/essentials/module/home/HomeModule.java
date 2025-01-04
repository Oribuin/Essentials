package dev.oribuin.essentials.module.home;

import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import org.bukkit.event.Listener;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.api.Module;
import dev.oribuin.essentials.api.config.ModuleConfig;
import dev.oribuin.essentials.manager.DataManager;
import dev.oribuin.essentials.module.home.command.HomeDeleteCommand;
import dev.oribuin.essentials.module.home.command.HomeSetCommand;
import dev.oribuin.essentials.module.home.command.HomeTPCommand;
import dev.oribuin.essentials.module.home.config.HomeConfig;
import dev.oribuin.essentials.module.home.config.HomeMessages;
import dev.oribuin.essentials.module.home.database.HomeRepository;

import java.util.List;

public class HomeModule extends Module implements Listener {

    private HomeRepository repository;

    /**
     * Create a new instance of the module
     *
     * @param plugin The plugin instance
     */
    public HomeModule(EssentialsPlugin plugin) {
        super(plugin);
    }

    /**
     * The name of the module
     * This will be used for logging and the name of the module.
     */
    @Override
    public String name() {
        return "homes";
    }

    /**
     * When the module is finished loading and is ready to be used.
     */
    @Override
    public void enable() {
        this.repository = DataManager.create(HomeRepository.class);

        if (this.repository == null) {
            this.logger.severe("The HomeRepository is null, this plugin will not work correctly.");
            EssentialsPlugin.unload(this);
            return;
        }

        this.repository.createTable();
    }

    /**
     * When the module is being disabled.
     */
    @Override
    public void disable() {
        if (this.repository != null) {
            this.repository.getHomes().clear();
        }
    }

    /**
     * Get all the commands for the module
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
     * Get all the configuration files for the module
     */
    @Override
    public List<ModuleConfig> configs() {
        return List.of(new HomeConfig(), new HomeMessages());
    }

    /**
     * Get the repository for the module
     */
    public HomeRepository getRepository() {
        return repository;
    }
}
