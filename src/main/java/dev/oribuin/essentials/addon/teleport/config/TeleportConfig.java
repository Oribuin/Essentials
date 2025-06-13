package dev.oribuin.essentials.addon.teleport.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.Option;

import java.util.List;

public class TeleportConfig extends AddonConfig {

    public static Option<Integer> COOLDOWN = new Option<>("cooldown", 5, "The cooldown for requesting a teleport (in seconds)");
    public static Option<Integer> REQUEST_TIMEOUT = new Option<>("request-timeout", 30, "How long in seconds should a teleport request last.");
    public static Option<Integer> TELEPORT_TIMER = new Option<>("teleport-timer", 5, "After accepting a teleport request, How many seconds should it take to teleport the player.");

    // Teleport Options
    public static Option<Integer> TP_DELAY = new Option<>("teleport-delay", 5, "The delay in seconds before teleporting to a another player.", "This will be overwritten with the permission 'essentials.tpa.bypass.delay'");
    public static Option<Integer> TP_COOLDOWN = new Option<>("teleport-cooldown", 30, "The cooldown in seconds between teleporting to another player.", "This will be overwritten with the permission 'essentials.tpa.bypass.cooldown'");
    public static Option<Double> TP_COST = new Option<>("teleport-cost", 0.0, "The cost to teleport to a another player.", "This will be overwritten with the permission 'essentials.tpa.bypass.cost'");
    public static Option<Boolean> TP_EFFECTS = new Option<>("teleport-effects", true, "Should the teleport effects be enabled?", "Effects will not be triggered if a player has 'essentials.tpa.bypass.delay'");
    public static Option<Boolean> TP_CONFIRM = new Option<>("teleport-confirm", true, "Should a player be required to confirm they want to teleport to their another player?");

    
    /**
     * Create a new instance of the addon config
     */
    public TeleportConfig() {
        super("config");
    }

}
