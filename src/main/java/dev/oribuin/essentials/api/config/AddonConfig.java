package dev.oribuin.essentials.api.config;

import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AddonConfig {

    public static final ConfigOption DEFAULT = new ConfigOption("enabled", true, List.of("Should the addon and all of its featured be enabled?"));

    private final String name;
    protected List<ConfigOption> options;
    protected CommentedFileConfiguration config;

    /**
     * Create a new instance of the addon config
     */
    public AddonConfig(String name) {
        this.name = name;
        this.options = new ArrayList<>();
    }

    /**
     * Load the configuration for the addon
     */
    public void load() {
        this.register(DEFAULT);
        this.registerClass();
    }

    /**
     * Load this config inside the addon folder
     *
     * @param addonFolder The folder of the addon
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void reload(File addonFolder) {
        // Clear the options and config
        this.options = new ArrayList<>();
        this.config = null;

        // Load the config values from the addon
        this.load();

        try {
            File configFile = new File(addonFolder, this.name + ".yml");
            if (!configFile.exists()) configFile.createNewFile();

            // Load the config
            this.config = CommentedFileConfiguration.loadConfiguration(configFile);
            for (ConfigOption option : this.options) {

                // If the config contains the path, cache the value
                if (this.config.contains(option.getPath())) {
                    option.setValue(new ConfigValue(this.config.get(option.getPath())));
                    continue;
                }

                // Add comments to the config
                if (!option.getComments().isEmpty()) {
                    for (String comment : option.getComments()) {
                        this.config.addPathedComments(option.getPath(), comment);
                    }

                    this.config.addPathedComments(option.getPath(), "Default: " + option.getDefaultValue().value());
                }

                // Set the default valueR
                this.config.set(option.getPath(), option.getDefaultValue().value());
                option.setValue(new ConfigValue(option.getDefaultValue()));
            }

            this.config.save(configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().severe("Failed to create config file for addon " + this.name);
        }
    }
    /**
     * Register all the options
     *
     * @param option The option to register
     */
    public final void register(ConfigOption option) {
        if (this.options.stream().anyMatch(opt -> opt.getPath().equalsIgnoreCase(option.getPath()))) {
            throw new IllegalArgumentException("Option with path " + option.getPath() + " already exists");
        }
        
        this.options.add(option);
    }

    /**
     * Find all the static ConfigOptions in the class and register them
     */
    public final void registerClass() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.getType().equals(ConfigOption.class)) continue;

            try {
                ConfigOption option = (ConfigOption) field.get(null);
                this.register(option);
            } catch (IllegalAccessException ex) {
                Bukkit.getLogger().severe("Failed to register ConfigOption in " + this.getClass().getSimpleName());
            }
        }
    }


    /**
     * Create a new ConfigOption with a default value
     *
     * @param path         The path of the config option
     * @param defaultValue The default value of the config option
     */
    public final void add(String path, Object defaultValue) {
        this.register(new ConfigOption(path, defaultValue));
    }

    /**
     * Create a new ConfigOption with comments
     *
     * @param path     The path of the config option
     * @param comments The comments for the config option
     */
    public final void add(String path, Object defaultValue, List<String> comments) {
        this.register(new ConfigOption(path, defaultValue, comments));
    }

    /**
     * Create a new ConfigOption with comments
     *
     * @param path     The path of the config option
     * @param comments The comments for the config option
     */
    public final void add(String path, Object defaultValue, String... comments) {
        this.register(new ConfigOption(path, defaultValue, List.of(comments)));
    }

    /**
     * Get a config option by its path
     *
     * @param path The path of the config option
     * @return The config option
     */
    @NotNull
    public final Optional<ConfigOption> get(@Nullable String path) {
        if (this.config == null || path == null) return Optional.empty();

        return this.options.stream()
                .filter(option -> option.getPath().equalsIgnoreCase(path))
                .findFirst();
    }

    /**
     * Get a config option by its path
     *
     * @param option The config option to get
     * @return The config option
     */
    public final Optional<ConfigOption> get(ConfigOption option) {
        if (this.config == null || option == null) return Optional.empty();

        return this.options.stream()
                .filter(opt -> opt.getPath().equalsIgnoreCase(option.getPath()))
                .findFirst();
    }

    /**
     * Get the name of the addon
     *
     * @return The name of the addon
     */
    public CommentedFileConfiguration getFile() {
        return this.config;
    }

}
