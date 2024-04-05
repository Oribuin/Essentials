package xyz.oribuin.essentials.module.teleport.config;

import xyz.oribuin.essentials.api.config.ConfigOption;
import xyz.oribuin.essentials.api.config.ModuleConfig;

public class TeleportConfig extends ModuleConfig {

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
        this.register(ConfigOption.enabled());

        this.add("cooldown", 5, "The cooldown for requesting a teleport.");
        this.add("request-timeout", 30, "How long in seconds should a teleport request last.");
        this.add("teleport-timer", 5, "After accepting a teleport request, How many seconds should it take to teleport the player.");
    }

}
