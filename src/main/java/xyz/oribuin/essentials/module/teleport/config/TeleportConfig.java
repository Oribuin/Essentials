package xyz.oribuin.essentials.module.teleport.config;

import xyz.oribuin.essentials.api.config.ConfigOption;
import xyz.oribuin.essentials.api.config.ModuleConfig;

public class TeleportConfig extends ModuleConfig {

    public static ConfigOption COOLDOWN = new ConfigOption("cooldown", 5, "The cooldown for requesting a teleport.");
    public static ConfigOption REQUEST_TIMEOUT = new ConfigOption("request-timeout", 30, "How long in seconds should a teleport request last.");
    public static ConfigOption TELEPORT_TIMER = new ConfigOption("teleport-timer", 5, "After accepting a teleport request, How many seconds should it take to teleport the player.");

    /**
     * Create a new instance of the module config
     */
    public TeleportConfig() {
        super("config");
    }

    /**
     * Load the configuration for the module
     */
    @Override
    public void load() {
        this.register(ModuleConfig.DEFAULT);
        this.registerClass();
    }

}
