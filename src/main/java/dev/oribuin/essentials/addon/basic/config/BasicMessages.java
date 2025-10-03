package dev.oribuin.essentials.addon.basic.config;

import dev.oribuin.essentials.addon.basic.BasicAddon;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.config.TextMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class BasicMessages implements AddonConfig {

    private static final String PREFIX = "<#bc7dff><b>Server</b> <gray>| <white>";

    public static BasicMessages get() {
        return BasicAddon.get().getConfigLoader().get(BasicMessages.class);
    }

    // Ping Command
    @Comment("The message sent when a player checks their own ping")
    private TextMessage pingSelf = new TextMessage(PREFIX + "Your ping is <#bc7dff><ping><white>ms");

    @Comment("The message sent when a player checks another player's ping")
    private TextMessage pingOther = new TextMessage(PREFIX + "<target>'s ping is <#bc7dff><ping>ms<white>");

    @Comment("The message sent when a player checks their own ping")
    private TextMessage pingAverage = new TextMessage(PREFIX + "The average ping of the server is <#bc7dff><ping><white>ms");

    // Gamemode Command
    @Comment("The message sent when a player changes their own gamemode")
    private TextMessage changeGamemode = new TextMessage(PREFIX + "You have changed your gamemode to <#bc7dff><gamemode><white>!");

    @Comment("The message sent when a player changes their own gamemode")
    private TextMessage changeGamemodeOther = new TextMessage(PREFIX + "You have changed <#bc7dff><target><white>'s gamemode to <#bc7dff><gamemode><white>!");

    // Fly Command
    @Comment("The short message for when a player's flight state is enabled")
    private String flyEnabledShorthand = "Enabled";

    @Comment("The short message for when a player's flight state is disabled")
    private String flyDisabledShorthand = "Disabled";

    @Comment("The message sent when a player changes their own flight state")
    private TextMessage flySelf = new TextMessage(PREFIX + "Your flight status is now set to <#bc7dff><status><white>!");

    @Comment("The message sent when a player changes another player's flight state")
    private TextMessage flyOther = new TextMessage(PREFIX + "You have set <#bc7dff><target><white>'s flight status to <#bc7dff><status><white>!");

    @Comment("The message sent when a player attempts to fly in a world where it's disabled")
    private TextMessage flyDisabledWorld = new TextMessage(PREFIX + "Flying is not allowed inside this world");

    // Feed Command
    @Comment("The message sent when a player feeds themselves")
    private TextMessage feedSelf = new TextMessage(PREFIX + "You are now fully satiated");

    @Comment("The message sent when a player feeds another player")
    private TextMessage feedOther = new TextMessage(PREFIX + "<#bc7dff><target><white> is now fully satiated");

    @Comment("The message sent when a player is on cooldown from using /feed")
    private TextMessage feedCooldown = new TextMessage(PREFIX + "You cannot feed as you are on cooldown");

    // Heal Command
    @Comment("The message sent when a player heals themselves")
    private TextMessage healSelf = new TextMessage(PREFIX + "You are now fully healed");

    @Comment("The message sent when a player heals another player")
    private TextMessage healOther = new TextMessage(PREFIX + "<#bc7dff><target><white> is now fully healed");

    @Comment("The message sent when a player is on cooldown from using /heal")
    private TextMessage healCooldown = new TextMessage(PREFIX + "You cannot heal as you are on cooldown");

    // Repair Command
    @Comment("The message sent when a player repair a single item")
    private TextMessage repairSingleCommand = new TextMessage(PREFIX + "You have repaired <item>!");
    
    @Comment("The message sent when a player repairs their items")
    private TextMessage repairCommand = new TextMessage(PREFIX + "You have repaired a total of <#bc7dff><amount><white> items!");

    @Comment("The message sent when a player is on cooldown from using /repair")
    private TextMessage repairCooldown = new TextMessage(PREFIX + "You cannot repair your items as you are on cooldown");

    @Comment("The message sent when a player tries to repair with no damaged items")
    private TextMessage repairNoDamage = new TextMessage(PREFIX + "None of the items in your inventory are damaged");

    // Weather Command
    @Comment("The message sent when a player changes the weather")
    private TextMessage weatherCommand = new TextMessage(PREFIX + "You have changed the weather in this world to <#bc7dff><weather><white>");

    @Comment("The message sent when a player changes their personal weather")
    private TextMessage playerWeather = new TextMessage(PREFIX + "You have changed your personal weather to <#bc7dff><weather><white>");

    @Comment("The message sent when a player changes their personal weather")
    private TextMessage playerWeatherOther = new TextMessage(PREFIX + "You have changed <#bc7dff><target><white>'s personal weather to <#bc7dff><weather><white>");

    @Comment("The message sent when a player teleports to the highest point in their world")
    private TextMessage topCommand = new TextMessage(PREFIX + "You have teleported to the highest point");

    @Comment("The message sent when a player clears their own inventory")
    private TextMessage clearInvSelf = new TextMessage(PREFIX + "You have cleared your own inventory");

    @Comment("The message sent when a player clears someone else's inventory")
    private TextMessage clearInvOther = new TextMessage(PREFIX + "You have cleared <#bc7dff><target><white>'s inventory");

    @Comment("The message sent when a player checks their own location")
    private TextMessage whereAmI = new TextMessage(PREFIX + "You are currently at <#bc7dff><x><white>,<#bc7dff><y><white>,<#bc7dff><z><white> in world <#bc7dff><world>");
    
    @Comment("The message sent when someone checks someone else's location")
    private TextMessage whereIsOther = new TextMessage(PREFIX + "<#bc7dff><target><white> are currently at <#bc7dff><x><white>,<#bc7dff><y><white>,<#bc7dff><z><white> in world <#bc7dff><world>");

    public TextMessage getPingSelf() {
        return pingSelf;
    }

    public TextMessage getPingOther() {
        return pingOther;
    }

    public TextMessage getPingAverage() {
        return pingAverage;
    }

    public TextMessage getChangeGamemode() {
        return changeGamemode;
    }

    public TextMessage getChangeGamemodeOther() {
        return changeGamemodeOther;
    }

    public String getFlyEnabledShorthand() {
        return flyEnabledShorthand;
    }

    public String getFlyDisabledShorthand() {
        return flyDisabledShorthand;
    }

    public TextMessage getFlySelf() {
        return flySelf;
    }

    public TextMessage getFlyOther() {
        return flyOther;
    }

    public TextMessage getFlyDisabledWorld() {
        return flyDisabledWorld;
    }

    public TextMessage getFeedSelf() {
        return feedSelf;
    }

    public TextMessage getFeedOther() {
        return feedOther;
    }

    public TextMessage getFeedCooldown() {
        return feedCooldown;
    }

    public TextMessage getHealSelf() {
        return healSelf;
    }

    public TextMessage getHealOther() {
        return healOther;
    }

    public TextMessage getHealCooldown() {
        return healCooldown;
    }

    public TextMessage getRepairSingleCommand() {
        return repairSingleCommand;
    }

    public TextMessage getRepairCommand() {
        return repairCommand;
    }

    public TextMessage getRepairCooldown() {
        return repairCooldown;
    }

    public TextMessage getRepairNoDamage() {
        return repairNoDamage;
    }

    public TextMessage getWeatherCommand() {
        return weatherCommand;
    }

    public TextMessage getPlayerWeather() {
        return playerWeather;
    }

    public TextMessage getPlayerWeatherOther() {
        return playerWeatherOther;
    }

    public TextMessage getTopCommand() {
        return topCommand;
    }

    public TextMessage getClearInvSelf() {
        return clearInvSelf;
    }

    public TextMessage getClearInvOther() {
        return clearInvOther;
    }

    public TextMessage getWhereAmI() {
        return whereAmI;
    }

    public TextMessage getWhereIsOther() {
        return whereIsOther;
    }
}
