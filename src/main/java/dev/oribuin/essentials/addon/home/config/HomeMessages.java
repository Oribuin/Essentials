package dev.oribuin.essentials.addon.home.config;

import dev.oribuin.essentials.api.config.option.Message;
import dev.oribuin.essentials.api.config.option.TextMessage;
import dev.oribuin.essentials.api.config.type.MessageConfig;

import java.util.List;

public class HomeMessages extends MessageConfig {

    private static final String PREFIX = "<#bc7dff><b>Homes</b> <gray>| <white>";

    public static Message HOME_SET = TextMessage.ofConfig(PREFIX + "You have created the home, [<#bc7dff><home><white>]", List.of("The message sent when a player sets a home."));
    public static Message HOME_DELETED = TextMessage.ofConfig(PREFIX + "You have deleted the home, [<#bc7dff><home><white>]", List.of("The message sent when a player deletes a home."));
    public static Message HOME_TELEPORT = TextMessage.ofConfig(PREFIX + "You are now teleporting to [<#bc7dff><home><white>]", List.of("The message sent when a player teleports to a home."));
    public static Message HOME_FORMAT = TextMessage.ofConfig(PREFIX + "<#bc7dff><name><gray>, ");
    public static Message HOME_LIMIT = TextMessage.ofConfig(PREFIX + "You have reached the maximum homes [<#bc7dff>$<amt>/<max><white>]", List.of("The message sent when a player reaches the maximum amount of homes."));
    public static Message HOME_COOLDOWN = TextMessage.ofConfig(PREFIX + "You must wait <time> before teleporting to a home.", List.of("The message sent when a player tries to teleport to a home on cooldown."));
    public static Message HOME_UNSAFE = TextMessage.ofConfig(PREFIX + "This home is unsafe to teleport to.", List.of("The message sent when a player tries to teleport to an unsafe home."));
    public static Message HOME_ALREADY_EXISTS = TextMessage.ofConfig(PREFIX + "A home by this name already exists.", List.of("The message sent when a player tries to set a home with a name that already exists in their list."));
    public static Message DISABLED_WORLD = TextMessage.ofConfig(PREFIX + "You cannot set homes in this world.", List.of("The message sent when a player tries to set a home in a disabled world."));
    public static Message INSUFFICIENT_FUNDS = TextMessage.ofConfig(PREFIX + "You do not have enough money to set a home.", List.of("The message sent when a player tries to set a home but does not have enough money."));
    public static Message TELEPORT_COST = TextMessage.ofConfig(PREFIX + "It cost you [<#bc7dff>$<cost><white>] to teleport to [<#bc7dff><home><white>]", List.of("The message sent when it cost money to teleport a player."));
    public static Message CONFIRM_COMMAND = TextMessage.ofConfig(PREFIX + "Please type the command again to confirm you want to teleport to [<#bc7dff><home><white>].", List.of("The message sent when a player needs to confirm their teleportation."));
    public static Message TELEPORT_FAILED = TextMessage.ofConfig(PREFIX + "The teleport was interrupted by an external factor.", List.of("Used when a different plugin cancels the ability for the player to teleport to their home."));

}
