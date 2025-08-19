package dev.oribuin.essentials.api.config.option;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.rosewood.rosegarden.config.RoseSetting;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.rosewood.rosegarden.config.SettingSerializers.SECTION;

public class ConfigSection extends ConfigOptionType<ConfigurationSection> {

    /**
     * Create a newly defined configuration section with comments
     *
     * @param name     The name of the section
     * @param comments The section description
     *
     * @return The returning config section
     */
    public static ConfigSection from(String name, String... comments) {
        ConfigurationSection section = RoseSetting.ofBackedSection(name, EssentialsPlugin.get(), comments).get();
        return new ConfigSection(name, section, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param path         The path to the config optionF
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public ConfigSection(@Nullable String path, @NotNull ConfigurationSection defaultValue, @NotNull List<String> comments) {
        super(path, SECTION, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param path         The path to the config option
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public ConfigSection(@Nullable String path, @NotNull ConfigurationSection defaultValue, @NotNull String... comments) {
        super(path, SECTION, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value
     *
     * @param path         The path to the config option
     * @param defaultValue The default values to use
     */
    public ConfigSection(@Nullable String path, @NotNull ConfigurationSection defaultValue) {
        super(path, SECTION, defaultValue);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public ConfigSection(@NotNull ConfigurationSection defaultValue, @NotNull List<String> comments) {
        super(SECTION, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public ConfigSection(@NotNull ConfigurationSection defaultValue, @NotNull String... comments) {
        super(SECTION, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param defaultValue The default values to use
     */
    public ConfigSection(@NotNull ConfigurationSection defaultValue) {
        super(SECTION, defaultValue);
    }
}
