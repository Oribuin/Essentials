package xyz.oribuin.essentials.module.home.config;

import xyz.oribuin.essentials.api.config.ConfigOption;
import xyz.oribuin.essentials.api.config.ModuleConfig;

@SuppressWarnings("unused")
public class HomeConfig extends ModuleConfig {

    // Module Options
    public static ConfigOption DISABLED_WORLDS = new ConfigOption("disabled-worlds", "world_nether", "world_the_end", "Homes cannot be set in these worlds.");
    public static ConfigOption MIN_HOMES = new ConfigOption("min-homes", 1, "The minimum amount of homes a player can have.", "This will be overwritten with the permission 'essentials.home.#");

    // Teleport Options
    public static ConfigOption TP_DELAY = new ConfigOption("teleport-delay", 0, "The delay in seconds before teleporting to a home.", "This will be overwritten with the permission 'essentials.home.bypass-delay'");
    public static ConfigOption TP_COOLDOWN = new ConfigOption("teleport-cooldown", 60, "The cooldown in seconds between teleporting to homes.", "This will be overwritten with the permission 'essentials.home.bypass-cooldown'");
    public static ConfigOption TP_COST = new ConfigOption("teleport-cost", 0.0, "The cost to teleport to a home.", "This will be overwritten with the permission 'essentials.home.bypass-cost'");
    public static ConfigOption TP_EFFECTS = new ConfigOption("teleport-effects", true, "Should the teleport effects be enabled?", "This will be overwritten with the permission 'essentials.home.bypass-effects'");

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
        // TODO: Make better, local file configs are WEIRD and not good but this is also really weird
        this.register(ModuleConfig.DEFAULT);
        this.registerClass();
    }

}
