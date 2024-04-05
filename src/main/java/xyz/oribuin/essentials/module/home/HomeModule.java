package xyz.oribuin.essentials.module.home;

import xyz.oribuin.essentials.EssentialsPlugin;
import xyz.oribuin.essentials.api.Module;

public class HomeModule extends Module {

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
        this.logger.info("Loading all the homes for all players.");
    }

    /**
     * When the module is being disabled.
     */
    @Override
    public void disable() {
        this.logger.info("Saving all the homes for all players.");
    }

}
