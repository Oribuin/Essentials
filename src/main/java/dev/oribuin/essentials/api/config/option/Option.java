package dev.oribuin.essentials.api.config.option;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Option<T> extends ConfigOptionType<T> {

    /**
     * Create a new ConfigOption with a default value and comments
     *
     * @param path         The path of the config option
     * @param defaultValue The default value of the config option
     * @param comments     The comments for the config option
     */
    public Option(@NotNull String path, @NotNull T defaultValue, @NotNull List<String> comments) {
        super(path, defaultValue, comments);
    }

    /**
     * Create a new ConfigOption with a default value and comments
     *
     * @param path         The path of the config option
     * @param defaultValue The default value of the config option
     * @param comments     The comments for the config option
     */
    public Option(@NotNull String path, @NotNull T defaultValue, String... comments) {
        super(path, defaultValue, comments);
    }

    /**
     * Create a new ConfigOption with a default value and comments
     *
     * @param defaultValue The default value of the config option
     * @param comments     The comments for the config option
     */
    public Option(@NotNull T defaultValue, @NotNull List<String> comments) {
        super(defaultValue, comments);
    }

    /**
     * Create a new ConfigOption with a default value and comments
     *
     * @param defaultValue The default value of the config option
     * @param comments     The comments for the config option
     */
    public Option(@NotNull T defaultValue, String... comments) {
        super(defaultValue, comments);
    }

    @Override
    public String toString() {
        return "Option{" +
               "path='" + path + '\'' +
               ", defaultValue=" + defaultValue +
               ", comments=" + comments +
               ", value=" + value +
               '}';
    }
}
