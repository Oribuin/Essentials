package dev.oribuin.essentials.addon.chat.channel;

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
     *
     * @return The audience if available
     */
    @Nullable Audience getAudience(@NotNull Player source);
}
