package dev.oribuin.essentials.addon.home.config;

import dev.oribuin.essentials.api.config.ConfigOption;
import dev.oribuin.essentials.api.config.AddonConfig;

public class HomeMessages extends AddonConfig {

    private static final String PREFIX = "<#bc7dff>&lHomes &8| &f";
    public static ConfigOption HOME_SET = new ConfigOption("home-set", PREFIX + "You have created the home, <#bc7dff>%home%&f.", "The message sent when a player sets a home.");
    public static ConfigOption HOME_DELETED = new ConfigOption("home-deleted", PREFIX + "You have deleted the home, <#bc7dff>%home%&f.", "The message sent when a player deletes a home.");
    public static ConfigOption HOME_TELEPORT = new ConfigOption("home-teleport", PREFIX + "Teleporting to <#bc7dff>%home%&f.", "The message sent when a player teleports to a home.");
    public static ConfigOption HOME_FORMAT = new ConfigOption("home-format", PREFIX + "<#bc7dff>%name%&7, ");
    public static ConfigOption HOME_LIMIT = new ConfigOption("home-limit", PREFIX + "You have reached the maximum homes %amt%/%max%", "The message sent when a player reaches the maximum amount of homes.");
    public static ConfigOption HOME_COOLDOWN = new ConfigOption("home-cooldown", PREFIX + "You must wait %time% before teleporting to a home.", "The message sent when a player tries to teleport to a home on cooldown.");
    public static ConfigOption HOME_UNSAFE = new ConfigOption("home-unsafe", PREFIX + "This home is unsafe to teleport to.", "The message sent when a player tries to teleport to an unsafe home.");
    public static ConfigOption DISABLED_WORLD = new ConfigOption("disabled-world", PREFIX + "You cannot set homes in this world.", "The message sent when a player tries to set a home in a disabled world.");
    public static ConfigOption MAX_HOMES = new ConfigOption("max-homes", PREFIX + "You have reached the maximum amount of homes.", "The message sent when a player tries to set a home when they have reached the maximum amount of homes.");
    public static ConfigOption INSUFFICIENT_FUNDS = new ConfigOption("insufficient-funds", PREFIX + "You do not have enough money to set a home.", "The message sent when a player tries to set a home but does not have enough money.");

    /**
     * Create a new instance of the addon config
     */
    public HomeMessages() {
        super("messages");
    }

    /**
     * Load the configuration for the addon
     */
    @Override
    public void load() {
        this.registerClass();
    }

}
