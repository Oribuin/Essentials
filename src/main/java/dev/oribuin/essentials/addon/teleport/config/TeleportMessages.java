package dev.oribuin.essentials.addon.teleport.config;

import dev.oribuin.essentials.api.config.option.Message;
import dev.oribuin.essentials.api.config.type.MessageConfig;

public class TeleportMessages extends MessageConfig {

    private static final String PREFIX = "<#bc7dff><b>Teleport</b> <gray>| <white>";

    public static Message DISABLED_WORLD = Message.of(PREFIX + "You are not allowed to teleport to the world.");
    public static Message TELEPORT_INVALID = Message.of(PREFIX + "This teleport request is no longer valid/doesn't exist");
    public static Message TELEPORT_TIMEOUT = Message.of(PREFIX + "Your teleport request to [<#bc7dff>%target%<white>] has timed out.");
    public static Message TELEPORT_TIMEOUT_OTHER = Message.of(PREFIX + "The teleport request from [<#bc7dff>%sender%<white>] has timed out.");
    public static Message TELEPORT_FAILED = Message.of(PREFIX + "Your teleport request was not successful. Please contact an admin about this.");
    public static Message TELEPORT_COST = Message.of(PREFIX + "It cost you [<#bc7dff>$%cost%<white>] to teleport to [<#bc7dff>%target%<white>]");
    public static Message TELEPORT_UNSAFE = Message.of(PREFIX + "The teleport location is not safe, this request has been cancelled.");
    public static Message INSUFFICIENT_FUNDS = Message.of(PREFIX + "You do not have enough money to accept this teleport");
    public static Message TELEPORT_SELF = Message.of(PREFIX + "You cannot send a teleport request to yourself");

    // Back Command
    public static Message TELEPORT_BACK = Message.of(PREFIX + "You have teleported back to your previous location");
    public static Message TELEPORT_BACK_INVALID = Message.of(PREFIX + "You do not have a location you previously teleported from");

    // Teleport Accept Messages
    public static Message TELEPORT_ACCEPT_SELF = Message.of(PREFIX + "You have accepted the request from [<#bc7dff>%sender%<white>]");
    public static Message TELEPORT_ACCEPT_OTHER = Message.of(PREFIX + "Your teleport request to [<#bc7dff>%target%<white>] has been accepted.");

    // Teleport Deny Messages
    public static Message TELEPORT_DENIED_SELF = Message.of(PREFIX + "You have denied the request from [<#bc7dff>%sender%<white>]");
    public static Message TELEPORT_DENIED_OTHER = Message.of(PREFIX + "Your teleport request to [<#bc7dff>%target%<white>] has been denied.");

    // Teleport Ask Messages
    public static Message TELEPORT_ASK_RECEIVED = Message.of("""
            
            <#bc7dff><b>Teleport</b> <gray>| <white>[<#bc7dff>%sender%<white>] wants to teleport to you.<reset>
            <#bc7dff><b>Teleport</b> <gray>| <white>
            <#bc7dff><b>Teleport</b> <gray>| <white><#0cf036><click:suggest_command:'/tpaccept'>[Accept]</click> <dark_gray>| <#f00c0c><click:suggest_command:'/tpdeny'>[Deny]</click>
                       \s""");

    public static Message TELEPORT_ASK_SENT = Message.of("""
            
            <#bc7dff><b>Teleport</b> <gray>| <white>You have requested to teleport to [<#bc7dff>%target%<white>]
            <#bc7dff><b>Teleport</b> <gray>| <white>
            <#bc7dff><b>Teleport</b> <gray>| <white><#f0720c><click:suggest_command:'/tpcancel'>[Cancel Request]</click>
                       \s""");

}
