package dev.oribuin.essentials.addon.teleport.config;

import dev.oribuin.essentials.api.config.option.Message;
import dev.oribuin.essentials.api.config.type.MessageConfig;

public class TeleportMessages extends MessageConfig {

    private static final String PREFIX = "<#bc7dff><b>Teleport</b> <gray>| <white>";

    public static Message DISABLED_WORLD = new Message("disabled-world", PREFIX + "You are not allowed to teleport to the world this player is in.");
    public static Message TELEPORT_INVALID = new Message("teleport-invalid", PREFIX + "This teleport request is no longer valid/doesn't exist");
    public static Message TELEPORT_TIMEOUT = new Message("teleport-timeout", PREFIX + "Your teleport request to [<#bc7dff>%target%<white>] has timed out.");
    public static Message TELEPORT_TIMEOUT_OTHER = new Message("teleport-timeout-other", PREFIX + "The teleport request from [<#bc7dff>%sender%<white>] has timed out.");
    public static Message TELEPORT_FAILED = new Message("teleport-failed", PREFIX + "Your teleport request to [<#bc7dff>%target%<white>] was not successful. Please contact an admin about this.");
    public static Message TELEPORT_COST = new Message("teleport-cost", PREFIX + "It cost you [<#bc7dff>$%cost%<white>] to teleport to [<#bc7dff>%target%<white>]", "The message sent when it cost money to teleport a player.");
    public static Message TELEPORT_UNSAFE = new Message("teleport-unsafe", PREFIX + "The teleport location is not safe, this request has been cancelled.");
    public static Message INSUFFICIENT_FUNDS = new Message("insufficient-funds", PREFIX + "You do not have enough money to accept this teleport", "The message sent when a player tries to set a home but does not have enough money.");

    // Teleport Accept Messages
    public static Message TELEPORT_ACCEPT_SELF = new Message("teleport-accept-self", PREFIX + "You have accepted the request from [<#bc7dff>%sender%<white>]");
    public static Message TELEPORT_ACCEPT_OTHER = new Message("teleport-accept-other", PREFIX + "Your teleport request to [<#bc7dff>%target%<white>] has been accepted.");
    
    // Teleport Deny Messages
    public static Message TELEPORT_DENIED_SELF = new Message("teleport-denied-self", PREFIX + "You have denied the request from [<#bc7dff>%sender%<white>]");
    public static Message TELEPORT_DENIED_OTHER = new Message("teleport-denied-other", PREFIX + "Your teleport request to [<#bc7dff>%target%<white>] has been denied.");

    // Teleport Ask Messages
    public static Message TELEPORT_ASK_RECEIVED = new Message("teleport-ask-received", """
            
            <#bc7dff><b>Teleport</b> <gray>| <white>[<#bc7dff>%sender%<white>] wants to teleport to you.<reset>
            <#bc7dff><b>Teleport</b> <gray>| <white>
            <#bc7dff><b>Teleport</b> <gray>| <white><#0cf036><click:suggest_command:'/tpaccept'>[Accept]</click> <dark_gray>| <#f00c0c><click:suggest_command:'/tpdeny'>[Deny]</click>
                       \s""");

    public static Message TELEPORT_ASK_SENT = new Message("teleport-ask-sent", """
            
            <#bc7dff><b>Teleport</b> <gray>| <white>You have requested to teleport to [<#bc7dff>%target%<white>]
            <#bc7dff><b>Teleport</b> <gray>| <white>
            <#bc7dff><b>Teleport</b> <gray>| <white><#f0720c><click:suggest_command:'/tpcancel'>[Cancel Request]</click>
                       \s""");

}
