package xyz.oribuin.essentials.module.home;

import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import xyz.oribuin.essentials.Essentials;
import xyz.oribuin.essentials.api.Module;
import xyz.oribuin.essentials.api.database.ModuleRepository;
import xyz.oribuin.essentials.manager.DataManager;
import xyz.oribuin.essentials.module.home.command.HomeCommand;
import xyz.oribuin.essentials.module.home.database.HomeRepository;

import java.util.List;

public class HomeModule extends Module {

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
        this.logger.info("Loading all the homes for all players.");
        this.repository = DataManager.create(HomeRepository.class);

        if (this.repository == null) {
            this.logger.severe("The HomeRepository is null, this plugin will not work correctly.");
            Essentials.unload(this);
            return;
        }

        this.repository.load();
    }

    /**
     * When the module is being disabled.
     */
    @Override
    public void disable() {
        this.logger.info("Saving all the homes for all players.");
    }

    /**
     * Get all the commands for the module
     */
    @Override
    public List<BaseRoseCommand> commands() {
        return List.of(new HomeCommand(this.plugin));
    }

}
