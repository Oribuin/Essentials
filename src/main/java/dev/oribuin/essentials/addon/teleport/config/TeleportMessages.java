package dev.oribuin.essentials.addon.teleport.config;

import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.config.TextMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class TeleportMessages implements AddonConfig {

    private static final String PREFIX = "<#bc7dff><b>Teleport</b> <gray>| <white>";

    public static TeleportMessages getInstance() {
        return TeleportAddon.getInstance().getConfigLoader().get(TeleportMessages.class);
    }

    private TextMessage disabledWorld = new TextMessage(PREFIX + "You are not allowed to teleport to the world.");
    private TextMessage teleportInvalid = new TextMessage(PREFIX + "This teleport request is no longer valid/doesn't exist");
    private TextMessage teleportTimeout = new TextMessage(PREFIX + "Your teleport request to [<#bc7dff><target><white>] has timed out");
    private TextMessage teleportTimeoutOther = new TextMessage(PREFIX + "The teleport request from [<#bc7dff><sender><white>] has timed out");

    private TextMessage teleportCost = new TextMessage(PREFIX + "It cost you [<#bc7dff>$<cost><white>] to teleport to [<#bc7dff><target><white>]");
    private TextMessage teleportUnsafe = new TextMessage(PREFIX + "The teleport location is not safe, this request has been cancelled.");
    private TextMessage insufficientFunds = new TextMessage(PREFIX + "You do not have enough money to accept this teleport");
    private TextMessage teleportSafe = new TextMessage(PREFIX + "You cannot send a teleport request to yourself");

    @Comment("The message sent when a player tries to teleport to another player while on cooldown.")
    private TextMessage teleportCooldown = new TextMessage(PREFIX + "You must wait <#bc7dff><time> <white>before you can request another teleport.");
    
    private TextMessage forceTeleportToOther = new TextMessage(PREFIX + "You have teleported yourself to <#bc7dff><target><white>");
    private TextMessage forceTeleportOthers = new TextMessage(PREFIX + "You have teleported <#bc7dff><target> <white>to <#bc7dff><destination>");
    private TextMessage teleportPosition = new TextMessage(PREFIX + "You have teleported yourself to <#bc7dff><x><white>,<#bc7dff><y><white>,<#bc7dff><z><white> in <#bc7dff><world>");
    // Back Command
    private TextMessage teleportBack = new TextMessage(PREFIX + "You have teleported back to your previous location");
    private TextMessage teleportBackInvalid = new TextMessage(PREFIX + "You do not have a location you previously teleported from");
    private TextMessage teleportBackOther = new TextMessage(PREFIX + "<target> has teleported back to their previous location");
    private TextMessage teleportBackInvalidOther = new TextMessage(PREFIX + "<target> does not have a location they previously teleported from");

    // Teleport Accept Messages
    private TextMessage teleportAccept = new TextMessage(PREFIX + "You have accepted the request from [<#bc7dff><sender><white>]");
    private TextMessage teleportAcceptOther = new TextMessage(PREFIX + "Your teleport request to [<#bc7dff><target><white>] has been accepted.");

    // Teleport Deny Messages
    private TextMessage teleportDenied = new TextMessage(PREFIX + "You have denied the request from [<#bc7dff><sender><white>]");
    private TextMessage teleportDeniedOther = new TextMessage(PREFIX + "Your teleport request to [<#bc7dff><target><white>] has been denied.");

    // Teleport Ask Messages
    private TextMessage teleportAskReceived = new TextMessage("""
            
            <#bc7dff><b>Teleport</b> <gray>| <white>[<#bc7dff><sender><white>] wants to teleport to you.<reset>
            <#bc7dff><b>Teleport</b> <gray>| <white>
            <#bc7dff><b>Teleport</b> <gray>| <white><#0cf036><click:suggest_command:'/tpaccept <sender>'>[Accept]</click> <dark_gray>| <#f00c0c><click:suggest_command:'/tpdeny'>[Deny]</click>
                       \s""");

    private TextMessage teleportAskSelf = new TextMessage("""
            
            <#bc7dff><b>Teleport</b> <gray>| <white>You have requested to teleport to [<#bc7dff><target><white>]
            <#bc7dff><b>Teleport</b> <gray>| <white>
            <#bc7dff><b>Teleport</b> <gray>| <white><#f0720c><click:suggest_command:'/tpcancel'>[Cancel Request]</click>
                       \s""");

    private TextMessage teleportHereAskReceived = new TextMessage("""
            
            <#bc7dff><b>Teleport</b> <gray>| <white>[<#bc7dff><sender><white>] wants to teleport to you to them.<reset>
            <#bc7dff><b>Teleport</b> <gray>| <white>
            <#bc7dff><b>Teleport</b> <gray>| <white><#0cf036><click:suggest_command:'/tpaccept <sender>'>[Accept]</click> <dark_gray>| <#f00c0c><click:suggest_command:'/tpdeny'>[Deny]</click>
                       \s""");

    private TextMessage teleportHereAskSelf = new TextMessage("""
            
            <#bc7dff><b>Teleport</b> <gray>| <white>You have requested to teleport to [<#bc7dff><target><white>] to you
            <#bc7dff><b>Teleport</b> <gray>| <white>
            <#bc7dff><b>Teleport</b> <gray>| <white><#f0720c><click:suggest_command:'/tpcancel'>[Cancel Request]</click>
                       \s""");

    // Teleport cancelled
    private TextMessage teleportCancelled = new TextMessage(PREFIX + "You have cancelled any outgoing teleport requests");
    
    public TextMessage getDisabledWorld() {
        return disabledWorld;
    }

    public TextMessage getForceTeleportToOther() {
        return forceTeleportToOther;
    }

    public TextMessage getForceTeleportOthers() {
        return forceTeleportOthers;
    }

    public TextMessage getTeleportPosition() {
        return teleportPosition;
    }

    public TextMessage getTeleportInvalid() {
        return teleportInvalid;
    }

    public TextMessage getTeleportTimeout() {
        return teleportTimeout;
    }

    public TextMessage getTeleportTimeoutOther() {
        return teleportTimeoutOther;
    }
    
    public TextMessage getTeleportCost() {
        return teleportCost;
    }

    public TextMessage getTeleportUnsafe() {
        return teleportUnsafe;
    }

    public TextMessage getInsufficientFunds() {
        return insufficientFunds;
    }

    public TextMessage getTeleportSafe() {
        return teleportSafe;
    }

    public TextMessage getTeleportCooldown() {
        return teleportCooldown;
    }

    public TextMessage getTeleportBack() {
        return teleportBack;
    }

    public TextMessage getTeleportBackInvalid() {
        return teleportBackInvalid;
    }

    public TextMessage getTeleportBackOther() {
        return teleportBackOther;
    }

    public TextMessage getTeleportBackInvalidOther() {
        return teleportBackInvalidOther;
    }

    public TextMessage getTeleportAccept() {
        return teleportAccept;
    }

    public TextMessage getTeleportAcceptOther() {
        return teleportAcceptOther;
    }

    public TextMessage getTeleportDenied() {
        return teleportDenied;
    }

    public TextMessage getTeleportDeniedOther() {
        return teleportDeniedOther;
    }

    public TextMessage getTeleportAskReceived() {
        return teleportAskReceived;
    }

    public TextMessage getTeleportAskSelf() {
        return teleportAskSelf;
    }

    public TextMessage getTeleportHereAskReceived() {
        return teleportHereAskReceived;
    }

    public TextMessage getTeleportHereAskSelf() {
        return teleportHereAskSelf;
    }

    public TextMessage getTeleportCancelled() {
        return teleportCancelled;
    }
}
