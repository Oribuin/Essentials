package xyz.oribuin.essentials.module.home;

import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import org.bukkit.event.Listener;
import xyz.oribuin.essentials.Essentials;
import xyz.oribuin.essentials.api.Module;
import xyz.oribuin.essentials.api.config.ModuleConfig;
import xyz.oribuin.essentials.manager.DataManager;
import xyz.oribuin.essentials.module.home.command.HomeDeleteCommand;
import xyz.oribuin.essentials.module.home.command.HomeSetCommand;
import xyz.oribuin.essentials.module.home.command.HomeTPCommand;
import xyz.oribuin.essentials.module.home.config.HomeConfig;
import xyz.oribuin.essentials.module.home.config.HomeMessages;
import xyz.oribuin.essentials.module.home.database.HomeRepository;

import java.util.List;

public class HomeModule extends Module implements Listener {

    private HomeRepository repository;

    /**
     * Create a new instance of the module
     *
     * @param plugin The plugin instance
     */
    public HomeModule(Essentials plugin) {
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
            Essentials.unload(this);
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
