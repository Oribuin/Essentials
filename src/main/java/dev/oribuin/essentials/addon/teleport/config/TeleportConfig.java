package dev.oribuin.essentials.addon.teleport.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.Option;
import dev.oribuin.essentials.command.argument.DurationArgumentHandler;

import java.time.Duration;

import static dev.oribuin.essentials.api.config.EssentialsSerializers.DURATION;
import static dev.rosewood.rosegarden.config.SettingSerializers.*;

public class TeleportConfig extends AddonConfig {

    public static Option<Boolean> DISABLE_INACCESSIBLE_TELEPORT = new Option<>(BOOLEAN, false, "Should players be prevented from teleporting into worlds they do not have access to (using permission essentials.teleport.<world>)");
    public static Option<Integer> REQUEST_TIMEOUT = new Option<>(INTEGER, 30, "How long in seconds should a teleport request last.");

    // Teleport Options
    public static Option<Duration> TP_DELAY = new Option<>(DURATION, Duration.ofSeconds(5), "The delay in seconds before teleporting to a another player.", "This will be overwritten with the permission 'essentials.tpa.bypass.delay'");
    public static Option<Duration> TP_COOLDOWN = new Option<>(DURATION, Duration.ofSeconds(30), "The cooldown in seconds between teleporting to another player.", "This will be overwritten with the permission 'essentials.tpa.bypass.cooldown'");
    public static Option<Boolean> TP_BAR = new Option<>(BOOLEAN, true, "Should the plugin display a bar counting until the player teleports (Requires tp-delay to be above 0)");
    public static Option<Double> TP_COST = new Option<>(DOUBLE, 0.0, "The cost to teleport to a another player.", "This will be overwritten with the permission 'essentials.tpa.bypass.cost'");


    /**
     * Create a new instance of the addon config
     */
    public TeleportConfig() {
        super("config");
    }

}
