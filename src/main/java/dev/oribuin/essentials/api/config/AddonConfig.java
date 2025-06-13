package dev.oribuin.essentials.api.config;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.api.config.option.ConfigOptionType;
import dev.oribuin.essentials.api.config.option.Option;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class AddonConfig {

    public static final Option<Boolean> ENABLED = new Option<>("enabled", true, "Should the addon and all of it's features be enabled?");

    private final String name;
    protected List<ConfigOptionType<?>> options;
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
        this.register(ENABLED);
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
            for (ConfigOptionType<?> option : this.options) {
                // If the config contains the path, cache the value
                if (this.config.contains(option.getPath())) {
                    option.setValue(this.config.get(option.getPath()));
                    continue;
                }

                // Add comments to the config
                if (!option.getComments().isEmpty()) {
                    for (String comment : option.getComments()) {
                        this.config.addPathedComments(option.getPath(), comment);
                    }

                    this.config.addPathedComments(option.getPath(), "Default: " + option.getDefaultValue());
                }

                // Set the default value
                this.config.set(option.getPath(), option.getDefaultValue());
                option.setValue(option.getDefaultValue());
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
    public final void register(ConfigOptionType<?> option) {
        if (this.options.stream().anyMatch(opt -> opt.getPath().equalsIgnoreCase(option.getPath()))) {
            throw new IllegalArgumentException("Option with path " + option.getPath() + " already exists");
        }

        this.options.add(option);
    }

    /**
     * Find all the static ConfigOptions in the class and register them
     */
    public final void registerClass() {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (!field.canAccess(null)) continue;

                Object type = field.get(null);

                if (type instanceof ConfigOptionType<?> option) {
                    this.register(option);
                }
            }
        } catch (IllegalAccessException ex) {
            EssentialsPlugin.get().getLogger().severe("Failed to register ConfigOption in " + this.getClass().getSimpleName() + " - " + ex);
        }
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
