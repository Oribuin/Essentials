package xyz.oribuin.essentials.api.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unchecked")
public class ConfigOption {

    private final @NotNull String path;
    private final @NotNull Object defaultValue;
    private final @NotNull List<String> comments;
    private @Nullable Object value;

    /**
     * Create a new ConfigOption with a default value and comments
     *
     * @param path         The path of the config option
     * @param defaultValue The default value of the config option
     * @param comments     The comments for the config option
     */
    public ConfigOption(@NotNull String path, @NotNull Object defaultValue, @NotNull List<String> comments) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.comments = comments;
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
     * Create a new ConfigOption with comments
     *
     * @param path     The path of the config option
     * @param comments The comments for the config option
     */
    public static ConfigOption of(String path, Object defaultValue, List<String> comments) {
        return new ConfigOption(path, defaultValue, comments);
    }

    /**
     * Create a new ConfigOption with a default value
     *
     * @param path         The path of the config option
     * @param defaultValue The default value of the config option
     */
    public static ConfigOption of(String path, Object defaultValue) {
        return new ConfigOption(path, defaultValue);
    }

    /**
     * The default ConfigOption for enabling/disabling a module
     *
     * @return The default ConfigOption for enabling/disabling a module
     */
    public static ConfigOption enabled() {
        return new ConfigOption("enabled", false, List.of("Should the module and all of its featured be enabled?"));
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
    public Object getDefaultValue() {
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
    public Object getValue() {
        return value;
    }

    /**
     * Get the value of the config option as a boolean
     */
    public boolean asBoolean() {
        return value instanceof Boolean result ? result : false;
    }

    /**
     * Get the value of the config option as a String
     */
    public String asString() {
        return value instanceof String result ? result : null;
    }

    /**
     * Get the value of the config option as an integer
     */
    public Integer asInt() {
        return value instanceof Integer result ? result : null;
    }

    /**
     * Get the value of the config option as a double
     */
    public Double asDouble() {
        return value instanceof Double result ? result : null;
    }

    /**
     * Get the value of the config option as a long
     */
    public Long asLong() {
        return value instanceof Long result ? result : null;
    }

    /**
     * Get the list of strings from the config option
     *
     * @return The list of strings
     */
    public List<String> asStringList() {
        return (List<String>) value;
    }

    /**
     * Get the value of the config option as a specific type
     *
     * @param type The type of the value
     * @param <T>  The type of the value
     * @return The value of the config option
     */
    public <T> T as(Class<T> type) {
        return type.cast(value);
    }

    /**
     * Set the value of the config option
     *
     * @param value The value of the config option
     */
    public void setValue(@Nullable Object value) {
        this.value = value;
    }

}
