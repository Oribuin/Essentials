package dev.oribuin.essentials.addon.teleport.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.Option;

public class TeleportConfig extends AddonConfig {

    public static Option<Integer> COOLDOWN = new Option<>("cooldown", 5, "The cooldown for requesting a teleport.");
    public static Option<Integer> REQUEST_TIMEOUT = new Option<>("request-timeout", 30, "How long in seconds should a teleport request last.");
    public static Option<Integer> TELEPORT_TIMER = new Option<>("teleport-timer", 5, "After accepting a teleport request, How many seconds should it take to teleport the player.");

    /**
     * Create a new instance of the addon config
     */
    public TeleportConfig() {
        super("config");
    }

}
