package dev.oribuin.essentials.addon.teleport.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.ConfigOption;

public class TeleportConfig extends AddonConfig {

    public static ConfigOption COOLDOWN = new ConfigOption("cooldown", 5, "The cooldown for requesting a teleport.");
    public static ConfigOption REQUEST_TIMEOUT = new ConfigOption("request-timeout", 30, "How long in seconds should a teleport request last.");
    public static ConfigOption TELEPORT_TIMER = new ConfigOption("teleport-timer", 5, "After accepting a teleport request, How many seconds should it take to teleport the player.");

    /**
     * Create a new instance of the addon config
     */
    public TeleportConfig() {
        super("config");
    }

}
