package dev.oribuin.essentials.addon.spawn.config;

import dev.oribuin.essentials.api.config.option.Message;
import dev.oribuin.essentials.api.config.option.TextMessage;
import dev.oribuin.essentials.api.config.type.MessageConfig;

import java.util.List;

public class SpawnMessages extends MessageConfig {

    private static final String PREFIX = "<#bc7dff><b>Spawn</b> <gray>| <white>";

    public static Message FIRST_JOIN_MESSAGE = TextMessage.ofPapiConfig(PREFIX + "Welcome, %player_name% to the server! (<#bc7dff>#%total_players%<white>)", List.of("The message that is sent when a player first joins the server."));
    public static Message JOIN_MESSAGE = TextMessage.ofPapiConfig("<#bc7dff>%player_name% joined.");
    public static Message LEAVE_MESSAGE = TextMessage.ofPapiConfig("<#bc7dff>%player_name% left.");
    public static Message TELEPORT_FAILED = TextMessage.ofConfig(PREFIX + "The teleport was interrupted by an external factor.");
    public static Message MOTD = TextMessage.ofPapiConfig("""
            
            <#bc7dff><b>Spawn</b> <gray>| <white>Welcome to the server, <#bc7dff>%player_name%<reset>
            <#bc7dff><b>Spawn</b> <gray>| <white>
            <#bc7dff><b>Spawn</b> <gray>| <white>Please read /rules to find out more information about the server.
                \s""");

}
