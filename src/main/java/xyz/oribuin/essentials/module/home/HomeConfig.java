package xyz.oribuin.essentials.module.home;

import xyz.oribuin.essentials.api.config.ModuleConfig;

public class HomeConfig extends ModuleConfig {

    /**
     * Create a new instance of the module config
     */
    public HomeConfig() {
        super("config");
    }

    /**
     * Load the configuration for the module
     */
    @Override
    public void load() {
        // TODO: Make better, system defined configs are WEIRD and not good but this is also really weird
        this.add("disabled-worlds", "world_nether", "world_the_end", "Homes cannot be set in these worlds.");
        this.add("min-homes", 1, "The minimum amount of homes a player can have.", "This will be overwritten with the permission 'essentials.home.#");
    }

}
