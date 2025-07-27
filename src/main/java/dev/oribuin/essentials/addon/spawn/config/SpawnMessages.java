package dev.oribuin.essentials.addon.spawn.config;

import dev.oribuin.essentials.api.config.option.Message;
import dev.oribuin.essentials.api.config.option.TextMessage;
import dev.oribuin.essentials.api.config.type.MessageConfig;

import java.util.List;

public class SpawnMessages extends MessageConfig {

    private static final String PREFIX = "<#bc7dff><b>Spawn</b> <gray>| <white>";
    
    // Spawn Messages
    public static Message SPAWN_TELEPORT = TextMessage.ofPapiConfig(PREFIX + "You are teleporting to spawn.");
    public static Message CONFIRM_COMMAND = TextMessage.ofConfig(PREFIX + "Please type the command again to teleport to spawn.");
    public static Message SET_SPAWN = TextMessage.ofPapiConfig(PREFIX + "You have changed the spawn to <#bc7dff><x>/<y>/<z><white>!");

    // Join Messages
    public static Message JOIN_MESSAGE = TextMessage.ofPapiConfig("<#bc7dff>%player_name% joined.");
    public static Message FIRST_JOIN_MESSAGE = TextMessage.ofPapiConfig(PREFIX + "Welcome, %player_name% to the server! (<#bc7dff>#<total_players><white>)", List.of("The message that is sent when a player first joins the server."));
    public static Message LEAVE_MESSAGE = TextMessage.ofPapiConfig("<#bc7dff>%player_name% left.");
    public static Message MOTD = TextMessage.ofPapiConfig("""
            
            <#bc7dff><b>Spawn</b> <gray>| <white>Welcome to the server, <#bc7dff>%player_name%<reset>
            <#bc7dff><b>Spawn</b> <gray>| <white>
            <#bc7dff><b>Spawn</b> <gray>| <white>Please read /rules to find out more information about the server.
                \s""");
    
    // Error Messages
    public static Message CONFIRM_TIMEOUT = TextMessage.ofConfig(PREFIX + "Your teleport request has timed out, please try again.");
    public static Message INSUFFICIENT_FUNDS = TextMessage.ofConfig(PREFIX + "You do not have enough money to teleport to spawn.", List.of("The message sent when a player tries to set a home but does not have enough money."));
    public static Message TELEPORT_FAILED = TextMessage.ofConfig(PREFIX + "The teleport was interrupted by an external factor.");
    public static Message TELEPORT_COOLDOWN = TextMessage.ofConfig(PREFIX + "You must wait <#bc7dff><time> <white>before teleporting to spawn.", List.of("The message sent when a player tries to teleport to spawn on cooldown."));

}
