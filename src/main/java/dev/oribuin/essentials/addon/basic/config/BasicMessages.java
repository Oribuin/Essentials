package dev.oribuin.essentials.addon.basic.config;

import dev.oribuin.essentials.api.config.option.Message;
import dev.oribuin.essentials.api.config.option.TextMessage;
import dev.oribuin.essentials.api.config.type.MessageConfig;

import java.util.List;

public class BasicMessages extends MessageConfig {

    private static final String PREFIX = "<#bc7dff><b>Server</b> <gray>| <white>";

    // Ping Command
    public static final Message PING_SELF = TextMessage.ofConfig(PREFIX + "Your ping is <#bc7dff>%ping%ms<white>.", List.of("The message sent when a player checks their own ping."));
    public static final Message PING_OTHER = TextMessage.ofConfig(PREFIX + "<target>'s ping is <#bc7dff><ping>ms<white>.", List.of("The message sent when a player checks another player's ping."));

    // Gamemode Command
    public static Message CHANGE_GAMEMODE = TextMessage.ofConfig(PREFIX + "You have changed your gamemode to <#bc7dff><gamemode><white>!", List.of("The message sent when a player changes their own gamemode."));
    public static Message CHANGE_GAMEMODE_OTHER = TextMessage.ofConfig(PREFIX + "You have changed <#bc7dff><target><white>/'s gamemode to <#bc7dff>%gamemode%<white>!", List.of("The message sent when a player changes their own gamemode."));

    // Fly Command
    public static Message FLY_SELF = TextMessage.ofConfig(PREFIX + "You have set your flight status to <#bc7dff><status><white>!");
    public static Message FLY_OTHER = TextMessage.ofConfig(PREFIX + "You have set <#bc7dff><player><white>'s flight status to <#bc7dff><status><white>!");
    public static Message FLY_DISABLED_WORLD = TextMessage.ofConfig(PREFIX + "Flying is not allowed inside this world.");

}
