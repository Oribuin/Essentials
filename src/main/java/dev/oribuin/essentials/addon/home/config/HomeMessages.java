package dev.oribuin.essentials.addon.home.config;

import dev.oribuin.essentials.api.config.option.Message;
import dev.oribuin.essentials.api.config.type.MessageConfig;

public class HomeMessages extends MessageConfig {

    private static final String PREFIX = "<#bc7dff><b>Homes</b> <gray>| <white>";

    public static Message HOME_SET = new Message("home-set", PREFIX + "You have created the home, <#bc7dff>%home%<white>.", "The message sent when a player sets a home.");
    public static Message HOME_DELETED = new Message("home-deleted", PREFIX + "You have deleted the home, <#bc7dff>%home%<white>.", "The message sent when a player deletes a home.");
    public static Message HOME_TELEPORT = new Message("home-teleport", PREFIX + "You are now teleporting to <#bc7dff>%home%<white>.", "The message sent when a player teleports to a home.");
    public static Message HOME_FORMAT = new Message("home-format", PREFIX + "<#bc7dff>%name%<gray>, ");
    public static Message HOME_LIMIT = new Message("home-limit", PREFIX + "You have reached the maximum homes %amt%/%max%", "The message sent when a player reaches the maximum amount of homes.");
    public static Message HOME_COOLDOWN = new Message("home-cooldown", PREFIX + "You must wait %time% before teleporting to a home.", "The message sent when a player tries to teleport to a home on cooldown.");
    public static Message HOME_UNSAFE = new Message("home-unsafe", PREFIX + "This home is unsafe to teleport to.", "The message sent when a player tries to teleport to an unsafe home.");
    public static Message HOME_ALREADY_EXISTS = new Message("home-already-exists", PREFIX + "A home by this name already exists.", "The message sent when a player tries to set a home with a name that already exists in their list.");
    public static Message DISABLED_WORLD = new Message("disabled-world", PREFIX + "You cannot set homes in this world.", "The message sent when a player tries to set a home in a disabled world.");
    public static Message INSUFFICIENT_FUNDS = new Message("insufficient-funds", PREFIX + "You do not have enough money to set a home.", "The message sent when a player tries to set a home but does not have enough money.");
    public static Message TELEPORT_COST = new Message("teleport-cost", PREFIX + "It cost you <#bc7dff>$%cost% <white>to teleport to <#bc7dff>%home%", "The message sent when it cost money to teleport a player.");
    public static Message CONFIRM_COMMAND = new Message("confirm-command", PREFIX + "Please type the command again to confirm you want to teleport to <#bc7dff>%home%<white>.", "The message sent when a player needs to confirm their teleportation.");
    public static Message TELEPORT_FAILED = new Message("teleport-failed", PREFIX + "The teleport was interrupted by an external factor.");

}
