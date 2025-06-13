package dev.oribuin.essentials.addon.basic.config;

import dev.oribuin.essentials.api.config.option.Message;
import dev.oribuin.essentials.api.config.type.MessageConfig;

public class BasicMessages extends MessageConfig {

    private static final String PREFIX = "<#bc7dff><b>Server</b> <gray>| <white>";

    // Ping Command
    public static final Message PING_SELF = new Message("ping-self", PREFIX + "Your ping is <#bc7dff>%ping%ms<white>.", "The message sent when a player checks their own ping.");
    public static final Message PING_OTHER = new Message("ping-other", PREFIX + "%target%'s ping is <#bc7dff>%ping%ms<white>.", "The message sent when a player checks another player's ping.");

    // Gamemode Command
    public static Message CHANGE_GAMEMODE = new Message("change-gamemode", PREFIX + "You have changed your gamemode to <#bc7dff>%gamemode%<white>!", "The message sent when a player changes their own gamemode.");
    public static Message CHANGE_GAMEMODE_OTHER = new Message("change-gamemode-other", PREFIX + "You have changed <#bc7dff>%target%<white>/'s gamemode to <#bc7dff>%gamemode%<white>!", "The message sent when a player changes their own gamemode.");
    
    // Fly Command
    public static Message FLY_SELF = new Message("fly-self", PREFIX + "You have set your flight status to <#bc7dff>%status%<white>!");
    public static Message FLY_OTHER = new Message("fly-other", PREFIX + "You have set <#bc7dff>%player%<white>'s flight status to <#bc7dff>%status%<white>!");
    public static Message FLY_DISABLED_WORLD = new Message("fly-disabled-world", PREFIX + "Flying is not allowed inside this world.");

}
