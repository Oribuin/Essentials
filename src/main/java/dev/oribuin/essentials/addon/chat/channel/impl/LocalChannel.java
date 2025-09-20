package dev.oribuin.essentials.addon.chat.channel.impl;

import dev.oribuin.essentials.addon.chat.channel.ChatChannel;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocalChannel implements ChatChannel {

    private static final int radius = 100; // TODO: ChatConfig#get.getLocalRadius()

    /**
     * The name of the chat channel to use
     *
     * @return The channel name
     */
    @Override
    public @NotNull String getName() {
        return "local";
    }

    /**
     * Get the target audience of the chat channel, deciding who will see it
     *
     * @param source The player sending the message to the audience
     *
     * @return The audience if available
     */
    @Override
    public @Nullable Audience getAudience(@NotNull Player source) {
        return this.filter(
                Audience.audience(
                        source.getWorld().getPlayers().stream()
                                .filter(x -> x.getLocation().distance(source.getLocation()) <= radius)
                                .toList()
                ), source);
    }

}
