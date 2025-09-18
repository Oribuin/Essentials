package dev.oribuin.essentials.config.impl;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.config.TextMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class PluginMessages {

    private static final String PREFIX = "<#bc7dff><b>Server</b> <gray>| <white>";

    public static PluginMessages getInstance() {
        return EssentialsPlugin.getInstance().getConfigLoader().get(PluginMessages.class);
    }

    @Comment("The message sent when a player does not have permission to do something.")
    private TextMessage noPermission = new TextMessage(PREFIX + "You do not have permission to do this");

    @Comment("The message sent when a player does not have permission to do something.")
    private TextMessage requirePlayer = new TextMessage(PREFIX + "You need to be sender type of <#bc7dff><sender><white> to run this command");

    @Comment("The message sent when a player gets the syntax for a message wrong")
    private TextMessage invalidSyntax = new TextMessage(PREFIX + "You have provided invalid syntax. The correct usage is: <#bc7dff><syntax>");

    public TextMessage getNoPermission() {
        return noPermission;
    }

    public TextMessage getRequirePlayer() {
        return requirePlayer;
    }

    public TextMessage getInvalidSyntax() {
        return invalidSyntax;
    }
}
