package dev.oribuin.essentials.api.config.option;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ConfigOptionType<T> {

    protected final @NotNull String path;
    protected final @NotNull T defaultValue;
    protected final @NotNull List<String> comments;
    protected @Nullable T value;

    /**
     * Create a new ConfigOption with a default value and comments
     *
     * @param path         The path of the config option
     * @param defaultValue The default value of the config option
     * @param comments     The comments for the config option
     */
    public ConfigOptionType(@NotNull String path, @NotNull T defaultValue, @NotNull List<String> comments) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.comments = comments;
    }

    /**
     * Create a new ConfigOption with a default value and comments
     *
     * @param path         The path of the config option
     * @param defaultValue The default value of the config option
     * @param comments     The comments for the config option
     */
    public ConfigOptionType(@NotNull String path, @NotNull T defaultValue, String... comments) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.comments = List.of(comments);
    }

    /**
     * Create a new ConfigOption with a default value
     *
     * @param path         The path of the config option
     * @param defaultValue The default value of the config option
     */
    public ConfigOptionType(@NotNull String path, @NotNull T defaultValue) {
        this(path, defaultValue, List.of());
    }

    /**
     * Get the value from the config option type or the default value
     *
     * @param elseOr The default value to load
     *
     * @return The value
     */
    public @NotNull T getValueOr(@NotNull T elseOr) {
        return this.value != null ? this.value : elseOr;
    }

    /**
     * Get the value from the config option type or the default value
     *
     * @return The value
     */
    public @NotNull T getValue() {
        return this.value != null ? this.value : this.defaultValue;
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
    public T getDefaultValue() {
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
     * Set the value of the config option
     *
     * @param value The value of the config option
     */
    @SuppressWarnings("unchecked")
    public void setValue(@Nullable Object value) {
        this.value = (T) value;
    }

}
