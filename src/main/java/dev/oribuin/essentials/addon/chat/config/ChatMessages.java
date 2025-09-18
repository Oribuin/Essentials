package dev.oribuin.essentials.addon.chat.config;

import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.config.TextMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class ChatMessages implements AddonConfig {

    private static final String PREFIX = "<#bc7dff><b>Chat</b> <gray>| <white>";

    public static ChatMessages getInstance() {
        return ChatAddon.getInstance().getConfigLoader().get(ChatMessages.class);
    }

    private TextMessage playerIgnored = new TextMessage(PREFIX + "You have ignored <#bc7dff><target><white>, You will no longer see their messages");
    private TextMessage playerUnignored = new TextMessage(PREFIX + "You have unignored <#bc7dff><target><white>, You will now see their messages");
    private TextMessage ignoreList = new TextMessage(PREFIX + "You have ignored <#bc7dff><total> <white>players. Including: <#bc7dff><ignored>");
    private TextMessage ignoreFormat = new TextMessage("<gray>, <#bc7dff>");
    private TextMessage cannotDmIgnored = new TextMessage(PREFIX + "You cannot message someone who is ignored");
    private TextMessage userMessagesClosed = new TextMessage(PREFIX + "This user has their private messages ignored");
    private TextMessage directMessageSent = new TextMessage("<white>You → <#bc7dff><recipient><gray>:<white> <message>");
    private TextMessage directMessageReceived = new TextMessage("<#bc7dff><sender> → <white>You<gray>:<white> <message>");
    private TextMessage socialSpyMessage = new TextMessage("<#9983f2><bold>SSpy</bold> <white>| <#bc7dff><sender> <white>→ <#bc7dff><recipient><gray>:<#9983f2> <message>");
    private TextMessage nobodyToReply = new TextMessage(PREFIX + "You do not have anybody to reply to");
    private TextMessage resetNickname = new TextMessage(PREFIX + "You have reset your nickname");
    private TextMessage resetNicknameOther = new TextMessage(PREFIX + "You have reset <#bc7dff><target><white>'s nickname");
    private TextMessage setNickname = new TextMessage(PREFIX + "You have changed your nickname to <nickname>");
    private TextMessage setNicknameOther = new TextMessage(PREFIX + "You have changed <#bc7dff><target><white>'s nickname to <nickname>");

    public TextMessage getPlayerIgnored() {
        return playerIgnored;
    }

    public TextMessage getPlayerUnignored() {
        return playerUnignored;
    }

    public TextMessage getIgnoreList() {
        return ignoreList;
    }

    public TextMessage getIgnoreFormat() {
        return ignoreFormat;
    }

    public TextMessage getCannotDmIgnored() {
        return cannotDmIgnored;
    }

    public TextMessage getUserMessagesClosed() {
        return userMessagesClosed;
    }

    public TextMessage getDirectMessageSent() {
        return directMessageSent;
    }

    public TextMessage getDirectMessageReceived() {
        return directMessageReceived;
    }

    public TextMessage getSocialSpyMessage() {
        return socialSpyMessage;
    }

    public TextMessage getNobodyToReply() {
        return nobodyToReply;
    }

    public TextMessage getResetNickname() {
        return resetNickname;
    }

    public TextMessage getResetNicknameOther() {
        return resetNicknameOther;
    }

    public TextMessage getSetNickname() {
        return setNickname;
    }

    public TextMessage getSetNicknameOther() {
        return setNicknameOther;
    }
}
