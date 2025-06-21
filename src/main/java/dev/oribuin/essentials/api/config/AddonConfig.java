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
import java.util.function.Predicate;

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
                if (option.getPath() == null) continue; // Ignore anything with no path 

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
     * Register an option into the config with option provided path
     *
     * @param option The option to register
     */
    public final void register(ConfigOptionType<?> option) {
        this.register(option, null);
    }

    /**
     * Register an option into the config with a default path if none exists
     *
     * @param option The option to register
     */
    public final void register(ConfigOptionType<?> option, String def) {
        String path = option.getPath() != null ? option.getPath() : def;
        if (path == null) {
            throw new IllegalArgumentException("Option [" + option + "] does not have a defined path");
        }

        if (this.options.stream().anyMatch(this.testPath(path))) {
            throw new IllegalArgumentException("Option with path " + path + " already exists");
        }

        option.setPath(path);
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
                String path = this.fieldToPath(field.getName());

                if (type instanceof ConfigOptionType<?> option) {
                    this.register(option, path);
                }
            }
        } catch (IllegalAccessException ex) {
            EssentialsPlugin.get().getLogger().severe("Failed to register ConfigOption in " + this.getClass().getSimpleName() + " - " + ex);
        }
    }

    /**
     * Convert the field name to a config available path
     * This could be changed to be more dynamic (allowing easily defined config options but eo\lwhrio)
     *
     * @param fieldName The field name to convert
     *
     * @return The string that got converted
     */
    private String fieldToPath(String fieldName) {
        return fieldName.toLowerCase().replaceAll("_", "-");
    }

    /**
     * Get the name of the addon
     *
     * @return The name of the addon
     */
    public CommentedFileConfiguration getFile() {
        return this.config;
    }

    private Predicate<ConfigOptionType<?>> testPath(String path) {
        return type -> type.getPath() != null && type.getPath().equalsIgnoreCase(path);
    }

}
