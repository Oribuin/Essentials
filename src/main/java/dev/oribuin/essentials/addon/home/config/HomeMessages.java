package dev.oribuin.essentials.addon.home.config;

import dev.oribuin.essentials.api.config.ConfigOption;
import dev.oribuin.essentials.api.config.MessageConfig;

public class HomeMessages extends MessageConfig {

    private static final String PREFIX = "<#bc7dff><b>Homes</b> <gray>| <white>";

    public static ConfigOption HOME_SET = new ConfigOption("home-set", PREFIX + "You have created the home, <#bc7dff>%home%<white>.", "The message sent when a player sets a home.");
    public static ConfigOption HOME_DELETED = new ConfigOption("home-deleted", PREFIX + "You have deleted the home, <#bc7dff>%home%<white>.", "The message sent when a player deletes a home.");
    public static ConfigOption HOME_TELEPORT = new ConfigOption("home-teleport", PREFIX + "You are now teleporting to <#bc7dff>%home%<white>.", "The message sent when a player teleports to a home.");
    public static ConfigOption HOME_FORMAT = new ConfigOption("home-format", PREFIX + "<#bc7dff>%name%&7, ");
    public static ConfigOption HOME_LIMIT = new ConfigOption("home-limit", PREFIX + "You have reached the maximum homes %amt%/%max%", "The message sent when a player reaches the maximum amount of homes.");
    public static ConfigOption HOME_COOLDOWN = new ConfigOption("home-cooldown", PREFIX + "You must wait %time% before teleporting to a home.", "The message sent when a player tries to teleport to a home on cooldown.");
    public static ConfigOption HOME_UNSAFE = new ConfigOption("home-unsafe", PREFIX + "This home is unsafe to teleport to.", "The message sent when a player tries to teleport to an unsafe home.");
    public static ConfigOption DISABLED_WORLD = new ConfigOption("disabled-world", PREFIX + "You cannot set homes in this world.", "The message sent when a player tries to set a home in a disabled world.");
    public static ConfigOption MAX_HOMES = new ConfigOption("max-homes", PREFIX + "You have reached the maximum amount of homes.", "The message sent when a player tries to set a home when they have reached the maximum amount of homes.");
    public static ConfigOption INSUFFICIENT_FUNDS = new ConfigOption("insufficient-funds", PREFIX + "You do not have enough money to set a home.", "The message sent when a player tries to set a home but does not have enough money.");
    public static ConfigOption TELEPORT_COST = new ConfigOption("teleport-cost", PREFIX + "It cost you <#bc7dff>$%cost% <white>to teleport to <#bc7dff>%home%", "The message sent when it cost money to teleport a player.");
    public static ConfigOption CONFIRM_COMMAND = new ConfigOption("confirm-command", PREFIX + "Please type the command again to confirm you want to teleport to <#bc7dff>%home%<white>.", "The message sent when a player needs to confirm their teleportation.");
    public static ConfigOption TELEPORT_FAILED = new ConfigOption("teleport-failed", PREFIX + "The teleport was interrupted by an external factor.");

}
