package dev.oribuin.essentials.api.config;

import dev.oribuin.essentials.util.EssUtils;
import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class ConfigOption {

    private final @NotNull String path;
    private final @NotNull ConfigValue defaultValue;
    private final @NotNull List<String> comments;
    private @Nullable ConfigValue value;

    /**
     * Create a new ConfigOption with a default value and comments
     *
     * @param path         The path of the config option
     * @param defaultValue The default value of the config option
     * @param comments     The comments for the config option
     */
    public ConfigOption(@NotNull String path, @NotNull Object defaultValue, @NotNull List<String> comments) {
        this.path = path;
        this.defaultValue = new ConfigValue(defaultValue);
        this.value = new ConfigValue(defaultValue);
        this.comments = comments;
    }


    /**
     * Create a new ConfigOption with a default value and comments
     *
     * @param path         The path of the config option
     * @param defaultValue The default value of the config option
     * @param comments     The comments for the config option
     */
    public ConfigOption(@NotNull String path, @NotNull Object defaultValue, String... comments) {
        this.path = path;
        this.defaultValue = new ConfigValue(defaultValue);
        this.comments = List.of(comments);
    }

    /**
     * Create a new ConfigOption with a default value
     *
     * @param path         The path of the config option
     * @param defaultValue The default value of the config option
     */
    public ConfigOption(@NotNull String path, @NotNull Object defaultValue) {
        this(path, defaultValue, List.of());
    }

    /**
     * Get the value of the config option or null;
     *
     * @param config The addon config
     * @return The value of the config option or null
     */
    @NotNull
    public ConfigValue get(AddonConfig config) {
        return config.get(this).map(ConfigOption::getValue).orElse(ConfigValue.empty());
    }

    /**
     * Get the value of the config option or the default value
     *
     * @param config The addon config
     * @return The value of the config option or the default value
     */
    @NotNull
    public ConfigValue getOrDef(AddonConfig config) {
        return config.get(this).map(ConfigOption::getValue).orElse(this.defaultValue);
    }

    /**
     * Get the value of the config option or the default value
     *
     * @param config The addon config
     * @param value  The default value
     * @return The value of the config option or the default value
     */
    @NotNull
    public ConfigValue getOr(AddonConfig config, Object value) {
        return config.get(this)
                .map(ConfigOption::getValue)
                .orElse(new ConfigValue(value));
    }

    /**
     * Send a message from the config to a CommandSender
     *
     * @param sender       The CommandSender to send the message to
     * @param placeholders The placeholders to apply to the message
     */
    public final void send(CommandSender sender, StringPlaceholders placeholders) {
        if (this.value == null || this.value.asString() == null) return;

        String message = this.value.asString();
        String parsed = PlaceholderAPIHook.applyPlaceholders(sender instanceof Player player ? player : null, placeholders.apply(message));

        // maybe async not necessary, but minimessage is not very optimised
        CompletableFuture.runAsync(() -> sender.sendMessage(EssUtils.kyorify(parsed)));
    }

    /**
     * Send a message from the config to a CommandSender using a config option
     *
     * @param sender The CommandSender to send the message to
     */
    public final void send(CommandSender sender) {
        this.send(sender, StringPlaceholders.empty());
    }

    /**
     * Create a new ConfigOption with comments
     *
     * @return The path of the config option
     */
    @NotNull
    public String getPath() {
        return path;
    }

    /**
     * Get the default value of the config option
     *
     * @return The default value of the config option
     */
    @NotNull
    public ConfigValue getDefaultValue() {
        return defaultValue;
    }

    /**
     * Get the comments for the config option
     *
     * @return The comments for the config option
     */
    @NotNull
    public List<String> getComments() {
        return comments;
    }

    /**
     * Get the value of the config option
     *
     * @return The value of the config option
     */
    @Nullable
    public ConfigValue getValue() {
        return value;
    }

    /**
     * Set the value of the config option
     *
     * @param value The value of the config option
     */
    public void setValue(@Nullable ConfigValue value) {
        this.value = value;
    }

}
