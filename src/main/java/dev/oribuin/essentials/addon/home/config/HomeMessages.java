package dev.oribuin.essentials.addon.home.config;

import dev.oribuin.essentials.addon.home.HomeAddon;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.config.TextMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class HomeMessages implements AddonConfig {

    private static final String PREFIX = "<#bc7dff><b>Homes</b> <gray>| <white>";

    public static HomeMessages getInstance() {
        return HomeAddon.getInstance().getConfigLoader().get(HomeMessages.class);
    }

    @Comment("The message sent when a player sets a home.")
    private TextMessage homeSet = new TextMessage(PREFIX + "You have created the home, [<#bc7dff><home><white>]");

    @Comment("The message sent when a player deletes a home.")
    private TextMessage homeDeleted = new TextMessage(PREFIX + "You have deleted the home, [<#bc7dff><home><white>]");

    @Comment("The message sent when a player teleports to a home.")
    private TextMessage homeTeleporting = new TextMessage(PREFIX + "You are now teleporting to [<#bc7dff><home><white>]");

    @Comment("The message sent when a player teleports to a home.")
    private TextMessage homeTeleportingOther = new TextMessage(PREFIX + "You are now teleporting to <#bc7dff><owner><white>'s home, [<#bc7dff><home><white>]");

    @Comment("The format of every home when listing them out in text")
    private TextMessage homeFormat = new TextMessage(PREFIX + "<#bc7dff><name><gray>, ");

    @Comment("The message sent when someone tries to add another home above their limit")
    private TextMessage homeLimit = new TextMessage(PREFIX + "You have reached the maximum homes [<#bc7dff>$<amt>/<max><white>]");

    @Comment("The message sent when a player tries to teleport to a home while on cooldown")
    private TextMessage homeCooldown = new TextMessage(PREFIX + "You must wait <time> before teleporting to a home.");

    @Comment("The message sent when a player tries to teleport to an unsafe home.")
    private TextMessage homeUnsafe = new TextMessage(PREFIX + "This home is unsafe to teleport to.");

    @Comment("The message sent when a player tries to set a home with a name that already exists in their list.")
    private TextMessage homeAlreadyExists = new TextMessage(PREFIX + "A home by this name already exists.");

    @Comment("The message sent when a player tries to set a home in a disabled world.")
    private TextMessage disabledWorld = new TextMessage(PREFIX + "You cannot set homes in this world.");

    @Comment("The message sent when a player tries to set a home but does not have enough money.")
    private TextMessage insufficientFunds = new TextMessage(PREFIX + "You do not have enough money to set a home.");

    @Comment("The message sent when it cost money to teleport a player.")
    private TextMessage teleportCost = new TextMessage(PREFIX + "It cost you [<#bc7dff>$<cost><white>] to teleport to [<#bc7dff><home><white>]");

    @Comment("The message sent when a player needs to confirm their teleportation.")
    private TextMessage confirmCommand = new TextMessage(PREFIX + "Please type the command again to confirm you want to teleport to [<#bc7dff><home><white>].");

    @Comment("Used when a different plugin cancels the ability for the player to teleport to their home.")
    private TextMessage teleportFailed = new TextMessage(PREFIX + "The teleport was interrupted by an external factor.");

    public TextMessage getHomeSet() {
        return homeSet;
    }

    public TextMessage getHomeDeleted() {
        return homeDeleted;
    }

    public TextMessage getHomeTeleporting() {
        return homeTeleporting;
    }

    public TextMessage getHomeTeleportingOther() {
        return homeTeleportingOther;
    }

    public TextMessage getHomeFormat() {
        return homeFormat;
    }

    public TextMessage getHomeLimit() {
        return homeLimit;
    }

    public TextMessage getHomeCooldown() {
        return homeCooldown;
    }

    public TextMessage getHomeUnsafe() {
        return homeUnsafe;
    }

    public TextMessage getHomeAlreadyExists() {
        return homeAlreadyExists;
    }

    public TextMessage getDisabledWorld() {
        return disabledWorld;
    }

    public TextMessage getInsufficientFunds() {
        return insufficientFunds;
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
