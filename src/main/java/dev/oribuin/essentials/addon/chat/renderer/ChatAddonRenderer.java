package dev.oribuin.essentials.addon.chat.renderer;

import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.addon.chat.config.ChatFormat;
import dev.oribuin.essentials.addon.chat.database.ChatSender;
import dev.oribuin.essentials.hook.plugin.PAPIProvider;
import dev.oribuin.essentials.util.EssUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatAddonRenderer implements io.papermc.paper.chat.ChatRenderer.ViewerUnaware {

    public static final MiniMessage FORMATTER = MiniMessage.miniMessage();
    public static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();
    public static final Map<String, TagResolver> RESOLVER_MAP = Map.of(
            "essentials.format.color", StandardTags.color(),
            "essentials.format.pride", StandardTags.pride(),
            "essentials.format.rainbow", StandardTags.rainbow(),
            "essentials.format.reset", StandardTags.reset(),
            "essentials.format.bold", TagResolver.resolver(Set.of("bold", "b"), (q, c) -> Tag.styling(TextDecoration.BOLD)),
            "essentials.format.underline", TagResolver.resolver(Set.of("underline", "u"), (q, c) -> Tag.styling(TextDecoration.UNDERLINED)),
            "essentials.format.italic", TagResolver.resolver(Set.of("italic", "i", "em"), (q, c) -> Tag.styling(TextDecoration.ITALIC)),
            "essentials.format.strikethrough", TagResolver.resolver(Set.of("strikethrough", "st"), (q, c) -> Tag.styling(TextDecoration.STRIKETHROUGH)),
            "essentials.format.obfuscated", TagResolver.resolver(Set.of("obfuscated", "obf"), (q, c) -> Tag.styling(TextDecoration.OBFUSCATED))
    );

    private final ChatAddon addon;

    public ChatAddonRenderer(ChatAddon addon) {
        this.addon = addon;
    }

    /**
     * Render out the chat format for a player with a specified message
     *
     * @param player      The player who's sending the message
     * @param displayName The player display name
     * @param message     The message being sent
     *
     * @return The chat format if available
     */
    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component displayName, @NotNull Component message) {
        ChatSender sender = this.addon.getRepository().get(player.getUniqueId());
        return render(player, sender, message);
    }

    /**
     * Render out the chat format for a player with a specified message
     *
     * @param player  The player who's sending the message
     * @param sender  The player chat data
     * @param message The message being sent
     *
     * @return The chat format if available
     */
    @NotNull
    public static Component render(@NotNull Player player, @NotNull ChatSender sender, @NotNull Component message) {
        ChatFormat chatFormat = sender.getFormat();
        if (chatFormat == null) return Component.empty();

        String format = EssUtils.replaceLegacy(PAPIProvider.apply(player, chatFormat.getFormat()));

        Component playerName = MiniMessage.builder().tags(TagResolver.resolver(
                        StandardTags.color(),
                        StandardTags.rainbow(),
                        StandardTags.gradient(),
                        StandardTags.pride(),
                        StandardTags.reset(),
                        StandardTags.decorations(TextDecoration.BOLD),
                        StandardTags.decorations(TextDecoration.ITALIC)
                )).build()
                .deserialize(sender.getNickname() != null ? sender.getNickname() : player.getName())
                .append(Component.empty());
        
        Component messageComponent = MiniMessage.builder().tags(TagResolver.builder()
                        .resolvers(getAvailable(player))
                        .build())
                .build()
                .deserialize(PLAIN.serialize(message));

        return FORMATTER.deserialize(PlaceholderAPI.setPlaceholders(player, format),
                Placeholder.component("player", playerName),
                Placeholder.component("message", messageComponent)
        );
    }

    private static List<TagResolver> getAvailable(Player player) {
        return RESOLVER_MAP.entrySet().stream()
                .filter(x -> player.hasPermission(x.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }

}
