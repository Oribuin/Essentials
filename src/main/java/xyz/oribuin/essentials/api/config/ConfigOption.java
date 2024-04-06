package xyz.oribuin.essentials.api.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
     * The default ConfigOption for enabling/disabling a module
     *
     * @return The default ConfigOption for enabling/disabling a module
     */
    public static ConfigOption enabled() {
        return new ConfigOption("enabled", false, List.of("Should the module and all of its featured be enabled?"));
    }

    /**
     * Get the value of the config option or null;
     *
     * @param config The module config
     * @return The value of the config option or null
     */
    public ConfigValue get(ModuleConfig config) {
        return config.get(this).map(ConfigOption::getValue).orElse(null);
    }

    /**
     * Get the value of the config option or the default value
     *
     * @param config The module config
     * @return The value of the config option or the default value
     */
    public ConfigValue getOrDef(ModuleConfig config) {
        return config.get(this).map(ConfigOption::getValue).orElse(this.defaultValue);
    }

    /**
     * Get the value of the config option or the default value
     *
     * @param config The module config
     * @param value  The default value
     * @return The value of the config option or the default value
     */
    public ConfigValue getOr(ModuleConfig config, Object value) {
        return config.get(this).map(ConfigOption::getValue).orElse(new ConfigValue(value));
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
