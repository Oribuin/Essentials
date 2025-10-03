package dev.oribuin.essentials.addon.chat.channel;

import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.addon.chat.database.ChatSender;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ChatChannel {

    /**
     * The name of the chat channel to use
     *
     * @return The channel name
     */
    @NotNull String getName();

    /**
     * Should the chat channel be enabled?
     *
     * @return Whether the chat channel should be available
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * Get the target audience of the chat channel, deciding who will see it
     *
     * @param source The player sending the message to the audience
     *:
     * @return The audience if available
     */
    @Nullable Audience getAudience(@NotNull Player source);

    /**
     * Filter out ignore status and muted channels from the viewer
     *
     * @param audience The audience to view
     * @param source   The source sending the message
     */
    default Audience filter(@NotNull Audience audience, @NotNull Player source) {
        return audience.filterAudience(aud -> {
            if (!(aud instanceof Player player)) return true;

            ChatSender viewer = ChatAddon.getInstance().getRepository().get(player.getUniqueId());
            if (viewer.getMutedChannels().contains(this)) return false;
            
            return !viewer.hasIgnored(source.getUniqueId());
        });
    }
    
}
