package dev.oribuin.essentials.addon.chat.channel;

import dev.oribuin.essentials.addon.chat.channel.impl.GlobalChannel;
import dev.oribuin.essentials.addon.chat.channel.impl.LocalChannel;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ChatChannels {

    private static final Map<String, ChatChannel> CHANNELS = new HashMap<>();
    public static final GlobalChannel GLOBAL = register(GlobalChannel::new);
    public static final LocalChannel LOCAL = register(LocalChannel::new);

    /**
     * Register a chat channel into the plugin
     *
     * @param supplier The chat channel supplier
     * @param <T>      The chat channel type
     *
     * @return The chat channel supplied
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T extends ChatChannel> T register(@NotNull Supplier<T> supplier) {
        T channel = supplier.get();
        if (channel == null) return (T) createEmpty();

        CHANNELS.put(channel.getName().toLowerCase(), channel);
        return channel;
    }

    /**
     * Get a chat channel from the list of registered channels
     *
     * @param channelClass The channel class to register
     * @param name         The name of the class
     * @param <T>          The type of chat channel
     *
     * @return The chat channel if available
     */
    @Nullable
    public static <T extends ChatChannel> T from(@NotNull Class<T> channelClass, @NotNull String name) {
        ChatChannel channel = CHANNELS.get(name);
        return channelClass.isInstance(channel) ? channelClass.cast(channel) : null;
    }

    /**
     * Get a chat channel from the list of registered channels
     *
     * @param name The name of the class
     *
     * @return The chat channel if available
     */
    @Nullable
    public static ChatChannel from(@NotNull String name) {
        return CHANNELS.get(name);
    }

    private static ChatChannel createEmpty() {
        return new ChatChannel() {
            @Override
            public @NotNull String getName() {
                return "empty";
            }

            @Override
            public @NotNull Audience getAudience(@NotNull Player source) {
                return Bukkit.getServer();
            }
        };
    }

}
