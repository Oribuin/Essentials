package dev.oribuin.essentials.addon.chat.database;

import dev.oribuin.essentials.addon.chat.channel.ChatChannel;
import dev.oribuin.essentials.addon.chat.channel.ChatChannels;
import dev.oribuin.essentials.addon.chat.config.ChatConfig;
import dev.oribuin.essentials.addon.chat.config.ChatFormat;
import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.serializer.def.DataTypes;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChatSender implements ForwardingAudience.Single {

    private static final Logger log = LoggerFactory.getLogger(ChatSender.class);
    private transient final UUID uuid;
    private transient @Nullable ChatSender lastMessaged;
    private transient boolean dirty;
    private @Nullable String nickname;
    private Set<UUID> ignoredUsers;
    private Set<ChatChannel> mutedChannels;
    private ChatChannel channel;
    private boolean openDms;
    private boolean socialSpy;

    public ChatSender(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.lastMessaged = null;
        this.dirty = false;
        this.nickname = null;
        this.ignoredUsers = new HashSet<>();
        this.mutedChannels = new HashSet<>();
        this.channel = ChatChannels.GLOBAL;
        this.openDms = true;
        this.socialSpy = false;
    }

    public @Nullable ChatFormat getFormat() {
        Player player = this.getPlayer();
        if (player == null) return null;
        
        return new ArrayList<>(ChatConfig.getInstance().getFormats()).stream()
                .sorted(Comparator.comparingInt(ChatFormat::getPriority))
                .filter(chatFormat -> {
                    // make sure it's the right channel
                    boolean channelCheck = chatFormat.getChannel().equalsIgnoreCase(this.channel.getName());

                    // make sure they have the right permission
                    boolean permissionCheck = player.hasPermission(chatFormat.getPermission());

                    return channelCheck && permissionCheck;
                })
                .findFirst()
                .orElse(null);
    }

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    /**
     * Get the player as an optional
     *
     * @return The player
     */
    public @NotNull Optional<Player> getPlayerOptional() {
        return Optional.ofNullable(Bukkit.getPlayer(this.uuid));
    }

    /**
     * Toggle the ignore status for a user in a chat sender
     *
     * @param uuid The uuid of the person being ignored or unignored
     *
     * @return The status of the toggle status
     */
    public boolean toggleIgnore(UUID uuid) {
        boolean result = this.ignoredUsers.add(uuid);
        if (!result) ignoredUsers.remove(uuid);

        dirty = true;
        return result;
    }

    /**
     * Check if the chat sender has a specific user ignored
     *
     * @param uuid The user to check
     *
     * @return huge if true
     */
    public boolean hasIgnored(UUID uuid) {
        return this.ignoredUsers.contains(uuid);
    }

    /**
     * Construct a  chat sender from a database row
     *
     * @param row The row to construct from
     *
     * @return The chat sender
     */
    public static ChatSender construct(QueryResult.Row row) {
        if (row == null) return null;
        ChatSender sender = new ChatSender(DataTypes.UUID.deserialize(row, "uuid"));
        List<String> muted = DataTypes.listType(DataTypes.STRING).deserialize(row, "mutedChannels");

        sender.setNickname(DataTypes.STRING.deserialize(row, "nickname"));
        sender.setIgnoredUsers(DataTypes.setType(DataTypes.UUID).deserialize(row, "ignoredUsers"));
        sender.setChannel(ChatChannels.from(DataTypes.STRING.deserialize(row, "channel")));
        sender.setOpenDms(DataTypes.BOOLEAN.deserialize(row, "openDms"));
        sender.setSocialSpy(DataTypes.BOOLEAN.deserialize(row, "socialSpy"));
        sender.setMutedChannels(muted.stream().map(ChatChannels::from).collect(Collectors.toSet()));
        return sender;
    }

    @Override
    public @NotNull Audience audience() {
        Player player = this.getPlayer();
        return player != null ? player : Audience.empty();
    }

    public UUID getUuid() {
        return uuid;
    }

    public @Nullable ChatSender getLastMessaged() {
        return lastMessaged;
    }

    public void setLastMessaged(@Nullable ChatSender lastMessaged) {
        this.lastMessaged = lastMessaged;
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public @Nullable String getNickname() {
        return nickname;
    }

    public void setNickname(@Nullable String nickname) {
        this.nickname = nickname;
        this.dirty = true;
    }

    public Set<UUID> getIgnoredUsers() {
        return ignoredUsers;
    }

    public void setIgnoredUsers(Set<UUID> ignoredUsers) {
        this.ignoredUsers = ignoredUsers;
        this.dirty = true;
    }

    public Set<ChatChannel> getMutedChannels() {
        return mutedChannels;
    }

    public void setMutedChannels(Set<ChatChannel> mutedChannels) {
        this.mutedChannels = mutedChannels;
    }

    public ChatChannel getChannel() {
        return channel;
    }

    public void setChannel(ChatChannel channel) {
        this.channel = channel;
        this.dirty = true;
    }

    public boolean isOpenDms() {
        return openDms;
    }

    public void setOpenDms(boolean openDms) {
        this.openDms = openDms;
        this.dirty = true;
    }

    public boolean isSocialSpy() {
        return socialSpy;
    }

    public void setSocialSpy(boolean socialSpy) {
        this.socialSpy = socialSpy;
        this.dirty = true;
    }

    public record UserSet(Set<UUID> users) {}
    public record ChannelSet(Set<String> channels) {}
    
}
