package dev.oribuin.essentials.config;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.hook.plugin.PAPIProvider;
import dev.oribuin.essentials.util.EssUtils;
import dev.oribuin.essentials.util.StringPlaceholders;
import dev.oribuin.essentials.util.model.Placeholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class TextMessage {

    private @Nullable String message;
    private @Nullable String actionbar;
    private @Nullable String sound;
    private @Nullable String titleHeader;
    private @Nullable String titleSubtitle;
    private transient @Nullable Sound.Source source;
    private Boolean placeholderapi;

    /**
     * Create a new message that can be sent to an audience with specified params
     *
     * @param message        The content that is sent to the player's chat
     * @param actionbar      The action bar message
     * @param sound          The sound that is sent
     * @param titleHeader    The title header
     * @param titleSubtitle  The title subtitle
     * @param placeholderapi Whether placeholderapi should be used when sending the message
     */
    public TextMessage(
            @Nullable String message,
            @Nullable String actionbar,
            @Nullable String sound,
            @Nullable String titleHeader,
            @Nullable String titleSubtitle,
            @Nullable String source,
            Boolean placeholderapi
    ) {
        this.message = message;
        this.actionbar = actionbar;
        this.sound = sound;
        this.titleHeader = titleHeader;
        this.titleSubtitle = titleSubtitle;
        this.source = EssUtils.getEnum(Sound.Source.class, source, null);
        this.placeholderapi = placeholderapi;
    }

    /**
     * Create a new message that can be sent to an audience with specified params
     */
    public TextMessage() {
        this(null, null, null, null, null, null, null);
    }

    /**
     * Create a new message that can be sent to an audience with specified params
     *
     * @param message The content that is sent to the player's chat
     */
    public TextMessage(@Nullable String message) {
        this(message, null, null, null, null, null, null);
    }

    /**
     * Create a new message that can be sent to an audience with specified params
     *
     * @param message        The content that is sent to the player's chat
     * @param placeholderapi The message will have placeholderapi enabled by default
     */
    public TextMessage(@Nullable String message, boolean placeholderapi) {
        this(message, null, null, null, null, null, placeholderapi);
    }

    /**
     * Send a message to player
     *
     * @param audience The audience to send the message to
     */
    public void send(Audience audience) {
        this.send(audience, null, Placeholders.empty());
    }

    /**
     * Send a message to players with
     *
     * @param audience The audience to send the message to
     * @param target   The placeholderapi target
     */
    public void send(Audience audience, Player target) {
        this.send(audience, target, Placeholders.empty());
    }

    /**
     * Send a message to players with
     *
     * @param audience     The audience to send the message to
     * @param placeholders The plugin defined placeholders
     */
    public void send(Audience audience, StringPlaceholders placeholders) {
        this.send(audience, null, placeholders);
    }

    /**
     * Send a message to players with
     *
     * @param audience     The audience to send the message to
     * @param placeholders The plugin defined placeholders
     */
    public void send(Audience audience, Object... placeholders) {
        this.send(audience, null, placeholders);
    }

    /**
     * Send a message to players with
     *
     * @param audience     The audience to send the message to
     * @param target       The placeholderapi target
     * @param placeholders The plugin defined placeholders
     */
    public void send(Audience audience, Player target, Object... placeholders) {
        StringPlaceholders.Builder builder = Placeholders.builder();
        for (int i = 0; i < placeholders.length; i += 2) {
            if (placeholders[i] instanceof String placeholder) {
                Object value = placeholders[i + 1];
                builder.add(placeholder, value);
            }
        }

        this.send(audience, target, builder.build());
    }


    /**
     * Send a message to players with
     *
     * @param audience     The audience to send the message to
     * @param target       The placeholderapi target
     * @param placeholders The plugin defined placeholders
     */
    public void send(Audience audience, Player target, StringPlaceholders placeholders) {
        if (this.message != null && !this.message.isEmpty()) {
            audience.sendMessage(this.parse(this.message, target, placeholders));
        }

        if (this.titleHeader != null) {
            Component header = this.parse(this.titleHeader, target, placeholders);
            Component subtitle = this.parse(this.titleSubtitle, target, placeholders);
            audience.showTitle(Title.title(header, subtitle));
        }

        if (this.actionbar != null) {
            audience.sendActionBar(this.parse(this.actionbar, target, placeholders));
        }

        if (sound != null && source != null) {
            Sound kyoriSound = Sound.sound(Key.key(this.sound), source, 1f, 1f);
            EssentialsPlugin.getInstance().getScheduler().runTask(() -> audience.playSound(kyoriSound));
        }
    }


    /**
     * Parse a message through the plugin and automatically decide whether placeholderapi should be involved
     *
     * @param message      The message being sent
     * @param target       The target of the placeholderapi values if available
     * @param placeholders Any possible Placeholders
     *
     * @return The formatted message
     */
    public Component parse(String message, Player target, StringPlaceholders placeholders) {
        if (message == null) return Component.empty();
        if (placeholders == null) placeholders = Placeholders.empty();

        boolean usePapi = this.placeholderapi != null ? this.placeholderapi : false;

        return usePapi
                ? EssUtils.kyorify(placeholders.apply(PAPIProvider.apply(target, message)))
                : EssUtils.kyorify(message, placeholders);
    }

    /**
     * Parse a message through the plugin and automatically decide whether placeholderapi should be involved
     *
     * @param message The message being sent
     * @param target  The target of the placeholderapi values if available
     *
     * @return The formatted message
     */
    public Component parse(String message, Player target) {
        return this.parse(message, target, Placeholders.empty());
    }

    /**
     * Parse a message through the plugin
     *
     * @param message      The message being sent
     * @param placeholders Any possible Placeholders
     *
     * @return The formatted message
     */
    public Component parse(String message, StringPlaceholders placeholders) {
        return this.parse(message, null, placeholders);
    }

    /**
     * Parse a message through the plugin
     *
     * @param message The message being sent
     *
     * @return The formatted message
     */
    public Component parse(String message) {
        return this.parse(message, null, Placeholders.empty());
    }

    public void apply(StringPlaceholders placeholders) {
        if (this.message != null) {
            this.message = placeholders.apply(this.message);
        }

        if (this.actionbar != null) {
            this.actionbar = placeholders.apply(this.actionbar);
        }

        if (this.titleHeader != null) {
            this.titleHeader = placeholders.apply(this.titleHeader);
        }

        if (this.titleSubtitle != null) {
            this.titleSubtitle = placeholders.apply(this.titleSubtitle);
        }
    }

    public @Nullable String message() {
        return message;
    }

    public TextMessage message(@Nullable String message) {
        this.message = message;
        return this;
    }

    public @Nullable String actionbar() {
        return actionbar;
    }

    public TextMessage actionbar(@Nullable String actionbar) {
        this.actionbar = actionbar;
        return this;
    }

    public @Nullable String sound() {
        return sound;
    }

    public TextMessage sound(@Nullable String sound) {
        this.sound = sound;
        return this;
    }

    public Sound.Source source() {
        return source;
    }

    public String sourceName() {
        return source != null ? source.name() : null;
    }

    public TextMessage source(Sound.Source source) {
        this.source = source;
        return this;
    }

    public @Nullable String titleHeader() {
        return titleHeader;
    }

    public TextMessage titleHeader(@Nullable String titleHeader) {
        this.titleHeader = titleHeader;
        return this;
    }

    public @Nullable String titleSubtitle() {
        return titleSubtitle;
    }

    public TextMessage titleSubtitle(@Nullable String titleSubtitle) {
        this.titleSubtitle = titleSubtitle;
        return this;
    }

    public Boolean papi() {
        return placeholderapi;
    }

    public TextMessage papi(boolean placeholderapi) {
        this.placeholderapi = placeholderapi;
        return this;
    }
}
