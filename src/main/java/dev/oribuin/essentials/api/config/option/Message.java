package dev.oribuin.essentials.api.config.option;

import dev.oribuin.essentials.util.EssUtils;
import dev.rosewood.rosegarden.config.RoseSettingSerializer;
import dev.rosewood.rosegarden.config.RoseSettingSerializers;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Message extends ConfigOptionType<String> {
    
    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param path         The path to the config option
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Message(@Nullable String path, @NotNull String defaultValue, @NotNull List<String> comments) {
        super(path, RoseSettingSerializers.STRING, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param path         The path to the config option
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Message(@Nullable String path, @NotNull String defaultValue, @NotNull String... comments) {
        super(path, RoseSettingSerializers.STRING, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value
     *
     * @param path         The path to the config option
     * @param defaultValue The default values to use
     */
    public Message(@Nullable String path, @NotNull String defaultValue) {
        super(path, RoseSettingSerializers.STRING, defaultValue);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Message(@NotNull String defaultValue, @NotNull List<String> comments) {
        super(RoseSettingSerializers.STRING, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Message(@NotNull String defaultValue, @NotNull String... comments) {
        super(RoseSettingSerializers.STRING, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param defaultValue The default values to use
     */
    public Message(@NotNull String defaultValue) {
        super(RoseSettingSerializers.STRING, defaultValue);
    }

    /**
     * Send a message from the config to a CommandSender
     *
     * @param sender       The CommandSender to send the message to
     * @param placeholders The placeholders to apply to the message
     */
    public final void send(Audience sender, StringPlaceholders placeholders) {
        // String parsed = placeholders.apply(PlaceholderAPIHook.applyPlaceholders(sender instanceof Player player ? player : null, message));

        // maybe async not necessary, but minimessage is not very optimised
        CompletableFuture.runAsync(() -> sender.sendMessage(EssUtils.kyorify(placeholders.apply(this.value))));
    }

    /**
     * Send a message from the config to a CommandSender
     *
     * @param sender       The CommandSender to send the message to
     * @param placeholders The placeholders to apply to the message
     */
    public final void send(Audience sender, Object... placeholders) {
        StringPlaceholders.Builder builder = StringPlaceholders.builder();
        for (int i = 0; i < placeholders.length; i += 2) {
            if (placeholders[i] instanceof String placeholder) {
                Object value = placeholders[i + 1];
                builder.add(placeholder, value);
            }
        }

        this.send(sender, builder.build());
    }

    /**
     * Send a message from the config to a CommandSender using a config option
     *
     * @param sender The CommandSender to send the message to
     */
    public final void send(Audience sender) {
        this.send(sender, StringPlaceholders.empty());
    }
    
}
