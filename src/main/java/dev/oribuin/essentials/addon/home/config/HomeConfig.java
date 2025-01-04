package dev.oribuin.essentials.addon.home.config;

import dev.oribuin.essentials.api.config.ConfigOption;
import dev.oribuin.essentials.api.config.AddonConfig;

import java.util.List;

@SuppressWarnings("unused")
public class HomeConfig extends AddonConfig {

    // Module Options
    public static ConfigOption DISABLED_WORLDS = new ConfigOption("disabled-worlds", List.of("world_nether", "world_the_end"), "Homes cannot be set in these worlds.");
    public static ConfigOption MIN_HOMES = new ConfigOption("min-homes", 1, "The minimum amount of homes a player can have.", "This will be overwritten with the permission 'essentials.home.#'");

    // Teleport Options
    public static ConfigOption TP_DELAY = new ConfigOption("teleport-delay", 5, "The delay in seconds before teleporting to a home.", "This will be overwritten with the permission 'essentials.home.bypass-delay'");
    public static ConfigOption TP_COOLDOWN = new ConfigOption("teleport-cooldown", 30, "The cooldown in seconds between teleporting to homes.", "This will be overwritten with the permission 'essentials.home.bypass-cooldown'");
    public static ConfigOption TP_COST = new ConfigOption("teleport-cost", 0.0, "The cost to teleport to a home.", "This will be overwritten with the permission 'essentials.home.bypass-cost'");
    public static ConfigOption TP_EFFECTS = new ConfigOption("teleport-effects", true, "Should the teleport effects be enabled?", "This will be overwritten with the permission 'essentials.home.bypass-effects'");

    // Home Setting Options
    public static ConfigOption SET_COST = new ConfigOption("set-cost", 0.0, "The cost to set a home.", "This will be overwritten with the permission 'essentials.home.bypass-set-cost'");

    /**
     * Create a new instance of the addon config
     */
    public HomeConfig() {
        super("config");
    }

    /**
     * Load the configuration for the addon
     */
    @Override
    public void load() {
        this.register(AddonConfig.DEFAULT);
        this.registerClass();
    }

}
