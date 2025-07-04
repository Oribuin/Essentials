package dev.oribuin.essentials.addon.home.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.Option;

import java.util.List;

import static dev.rosewood.rosegarden.config.SettingSerializers.*;

public class HomeConfig extends AddonConfig {

    // Module Options
    public static Option<List<String>> DISABLED_WORLDS = new Option<>(STRING_LIST, List.of("world_nether", "world_the_end"), "Homes cannot be set in these worlds.");

    // Teleport Options
    public static Option<Integer> TP_DELAY = new Option<>(INTEGER, 5, "The delay in seconds before teleporting to a home.", "This will be overwritten with the permission 'essentials.home.bypass.delay'");
    public static Option<Integer> TP_COOLDOWN = new Option<>(INTEGER, 30, "The cooldown in seconds between teleporting to homes.", "This will be overwritten with the permission 'essentials.home.bypass.cooldown'");
    public static Option<Double> TP_COST = new Option<>(DOUBLE, 0.0, "The cost to teleport to a home.", "This will be overwritten with the permission 'essentials.home.bypass.cost'");
    public static Option<Boolean> TP_EFFECTS = new Option<>(BOOLEAN, true, "Should the teleport effects be enabled?", "Effects will not be triggered if a player has 'essentials.home.bypass.delay'");
    public static Option<Boolean> TP_CONFIRM = new Option<>(BOOLEAN, true, "Should a player be required to confirm they want to teleport to their home?");

    // Home Setting Options
    public static Option<Double> SET_COST = new Option<>(DOUBLE, 0.0, "The cost to set a home.", "This will be overwritten with the permission 'essentials.home.bypass-set-cost'");

    /**
     * Create a new instance of the addon config
     */
    public HomeConfig() {
        super("config");
    }

}
