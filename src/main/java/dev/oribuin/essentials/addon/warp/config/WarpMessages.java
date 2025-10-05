package dev.oribuin.essentials.addon.warp.config;

import dev.oribuin.essentials.addon.warp.WarpsAddon;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.config.TextMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class WarpMessages implements AddonConfig {

    private static final String PREFIX = "<#bc7dff><b>Warps</b> <gray>| <white>";

    public static WarpMessages getInstance() {
        return WarpsAddon.getInstance().getConfigLoader().get(WarpMessages.class);
    }

    @Comment("The message sent when a player sets a warp.")
    private TextMessage warpSet = new TextMessage(PREFIX + "You have created the warp, [<#bc7dff><warp><white>]");

    @Comment("The message sent when a player deletes a warp.")
    private TextMessage warpDeleted = new TextMessage(PREFIX + "You have deleted the warp, [<#bc7dff><warp><white>]");

    @Comment("The message sent when a player teleports to a warp.")
    private TextMessage warpTeleporting = new TextMessage(PREFIX + "You are now teleporting to [<#bc7dff><warp><white>]");
    
    @Comment("The format of every warp when listing them out in text")
    private TextMessage warpFormat = new TextMessage(PREFIX + "<#bc7dff><name><gray>, ");
    
    @Comment("The message sent when a player tries to teleport to a warp while on cooldown")
    private TextMessage warpCooldown = new TextMessage(PREFIX + "You must wait <time> before teleporting to a warp.");

    @Comment("The message sent when a player tries to teleport to an unsafe warp.")
    private TextMessage warpUnsafe = new TextMessage(PREFIX + "This warp is unsafe to teleport to.");

    @Comment("The message sent when a player tries to set a warp with a name that already exists in their list.")
    private TextMessage warpAlreadyExists = new TextMessage(PREFIX + "A warp by this name already exists.");

    @Comment("The message sent when a player tries to set a warp in a disabled world.")
    private TextMessage disabledWorld = new TextMessage(PREFIX + "You cannot set warps in this world.");
    
    @Comment("The message sent when it cost money to teleport a player.")
    private TextMessage teleportCost = new TextMessage(PREFIX + "It cost you [<#bc7dff>$<cost><white>] to teleport to [<#bc7dff><warp><white>]");

    @Comment("The message sent when a player needs to confirm their teleportation.")
    private TextMessage confirmCommand = new TextMessage(PREFIX + "Please type the command again to confirm you want to teleport to [<#bc7dff><warp><white>].");

    @Comment("Used when a different plugin cancels the ability for the player to teleport to their warp.")
    private TextMessage teleportFailed = new TextMessage(PREFIX + "The teleport was interrupted by an external factor.");

    public TextMessage getWarpSet() {
        return warpSet;
    }

    public TextMessage getWarpDeleted() {
        return warpDeleted;
    }

    public TextMessage getWarpTeleporting() {
        return warpTeleporting;
    }

    public TextMessage getWarpFormat() {
        return warpFormat;
    }

    public TextMessage getWarpCooldown() {
        return warpCooldown;
    }

    public TextMessage getWarpUnsafe() {
        return warpUnsafe;
    }

    public TextMessage getWarpAlreadyExists() {
        return warpAlreadyExists;
    }

    public TextMessage getDisabledWorld() {
        return disabledWorld;
    }

    public TextMessage getTeleportCost() {
        return teleportCost;
    }

    public TextMessage getConfirmCommand() {
        return confirmCommand;
    }

    public TextMessage getTeleportFailed() {
        return teleportFailed;
    }
}
