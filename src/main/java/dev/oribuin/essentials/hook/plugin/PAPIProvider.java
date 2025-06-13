package dev.oribuin.essentials.hook.plugin;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIProvider extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "essentials";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Oribuin";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    /**
     * Apply the PlaceholderAPI to the given text. If the player is null, it will return the text as is.
     *
     * @param player The player to apply the PlaceholderAPI to.
     * @param text   The text to apply the PlaceholderAPI to.
     *
     * @return The text with the PlaceholderAPI applied.
     */
    public static String apply(@Nullable OfflinePlayer player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

}
