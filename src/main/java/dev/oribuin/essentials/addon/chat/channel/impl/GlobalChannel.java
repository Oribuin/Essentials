package dev.oribuin.essentials.addon.chat.channel.impl;

import dev.oribuin.essentials.addon.chat.channel.ChatChannel;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlobalChannel implements ChatChannel {

    /**
     * The name of the chat channel to use
     *
     * @return The channel name
     */
    @Override
    public @NotNull String getName() {
        return "global";
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
        return this.filter(Bukkit.getServer(), source);
    }

}
