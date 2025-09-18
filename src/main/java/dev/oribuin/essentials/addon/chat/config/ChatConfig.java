package dev.oribuin.essentials.addon.chat.config;

import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.addon.chat.channel.ChatChannels;
import dev.oribuin.essentials.config.AddonConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class ChatConfig implements AddonConfig {

    public static ChatConfig getInstance() {
        return ChatAddon.getInstance().getConfigLoader().get(ChatConfig.class);
    }

    @Comment("Whether the basic addon module is enabled")
    private boolean enabled = true;

    private int maxNickLength = 16;
    private List<ChatFormat> formats = new ArrayList<>(List.of(
            new ChatFormat(ChatChannels.GLOBAL),
            new ChatFormat(ChatChannels.LOCAL, "<#3b94ed%><bold>Local <white>| %vault_prefix% <white>%player_name% <reset><!italic><gray>» <white><message>")
    ));

    private List<Pattern> chatfilter = new ArrayList<>(List.of(
            Pattern.compile("(?:https?://.)?(?:www\\.)?[-a-zA-Z0-9@%._+~#=]{2,256}\\.[a-z]{2,6}\\b(?:[-a-zA-Z0-9@:%_+.~#?&/=]*)")
    ));

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public int getMaxNickLength() {
        return maxNickLength;
    }

    public List<ChatFormat> getFormats() {
        return formats;
    }

    public List<Pattern> getChatfilter() {
        return chatfilter;
    }
}
