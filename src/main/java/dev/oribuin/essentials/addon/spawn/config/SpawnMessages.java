package dev.oribuin.essentials.addon.spawn.config;

import dev.oribuin.essentials.addon.spawn.SpawnAddon;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.config.TextMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class SpawnMessages implements AddonConfig {

    private static final String PREFIX = "<#bc7dff><b>Spawn</b> <gray>| <white>";

    public static SpawnMessages getInstance() {
        return SpawnAddon.getInstance().getConfigLoader().get(SpawnMessages.class);
    }

    // Spawn Messages
    private TextMessage spawnTeleport = new TextMessage(PREFIX + "You are teleporting to spawn.");
    private TextMessage spawnTeleportOther = new TextMessage(PREFIX + "You are teleporting <#bc7dff><player><white> to the spawn");

    private TextMessage confirmCommand = new TextMessage(PREFIX + "Please type the command again to teleport to spawn.");
    private TextMessage setSpawn = new TextMessage(PREFIX + "You have changed the spawn to <#bc7dff><x>/<y>/<z><white>!", true);

    // Join Messages
    private TextMessage joinMessage = new TextMessage("<#bc7dff>%player_name% joined.", true);

    @Comment("The message that is sent when a player first joins the server.")
    private TextMessage firstJoinMessage = new TextMessage(PREFIX + "Welcome, %player_name% to the server! (<#bc7dff>#<total_players><white>)", true);
    private TextMessage leaveMessage = new TextMessage("<#bc7dff>%player_name% left.", true);
    private TextMessage motd = new TextMessage("""
            
            <#bc7dff><b>Spawn</b> <gray>| <white>Welcome to the server, <#bc7dff>%player_name%<reset>
            <#bc7dff><b>Spawn</b> <gray>| <white>
            <#bc7dff><b>Spawn</b> <gray>| <white>Please read /rules to find out more information about the server.
                \s""", true);

    // Error Messages
    private TextMessage confirmTimeout = new TextMessage(PREFIX + "Your teleport request has timed out, please try again.");

    @Comment("The message sent when a player tries to set a home but does not have enough money.")
    private TextMessage insufficientAmount = new TextMessage(PREFIX + "You do not have enough money to teleport to spawn.", true);
    private TextMessage teleportFailed = new TextMessage(PREFIX + "The teleport was interrupted by an external factor.");

    @Comment("The message sent when a player tries to teleport to spawn on cooldown.")
    private TextMessage teleportCooldown = new TextMessage(PREFIX + "You must wait <#bc7dff><time> <white>before teleporting to spawn.");

    public TextMessage getSpawnTeleport() {
        return spawnTeleport;
    }

    public TextMessage getSpawnTeleportOther() {
        return spawnTeleportOther;
    }

    public TextMessage getConfirmCommand() {
        return confirmCommand;
    }

    public TextMessage getSetSpawn() {
        return setSpawn;
    }

    public TextMessage getJoinMessage() {
        return joinMessage;
    }

    public TextMessage getFirstJoinMessage() {
        return firstJoinMessage;
    }

    public TextMessage getLeaveMessage() {
        return leaveMessage;
    }

    public TextMessage getMotd() {
        return motd;
    }

    public TextMessage getConfirmTimeout() {
        return confirmTimeout;
    }

    public TextMessage getInsufficientAmount() {
        return insufficientAmount;
    }

    public TextMessage getTeleportFailed() {
        return teleportFailed;
    }

    public TextMessage getTeleportCooldown() {
        return teleportCooldown;
    }
}
