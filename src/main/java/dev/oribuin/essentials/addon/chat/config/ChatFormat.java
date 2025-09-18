package dev.oribuin.essentials.addon.chat.config;

import dev.oribuin.essentials.addon.chat.channel.ChatChannel;
import dev.oribuin.essentials.addon.chat.channel.ChatChannels;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class ChatFormat {

    private String format;
    private String channel;
    private String permission;
    private int priority;

    /**
     * Create a new chat channel
     */
    public ChatFormat() {
        this(ChatChannels.GLOBAL);
    }

    /**
     * Create a new chat channel
     *
     * @param channel The chat channel
     */
    public ChatFormat(ChatChannel channel) {
        this(channel, "%vault_prefix% <white>%player_name% <reset><!italic><gray>» <white><message>");
    }

    /**
     * Create a new chat channel
     *
     * @param channel The chat channel
     * @param format  The chat format style
     */
    public ChatFormat(ChatChannel channel, String format) {
        this.format = format;
        this.channel = channel.getName().toLowerCase();
        this.permission = "essentials.chatformat.default";
        this.priority = 0;
    }

    public String getFormat() {
        return format;
    }

    public String getChannel() {
        return channel;
    }

    public String getPermission() {
        return permission;
    }

    public int getPriority() {
        return priority;
    }
}
