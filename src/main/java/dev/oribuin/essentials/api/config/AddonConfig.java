package dev.oribuin.essentials.api.config;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.api.config.option.ConfigOptionType;
import dev.oribuin.essentials.api.config.option.Option;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static dev.rosewood.rosegarden.config.SettingSerializers.BOOLEAN;

@SuppressWarnings("unused")
public abstract class AddonConfig {

    public static final Option<Boolean> ENABLED = new Option<>(
            BOOLEAN,
            true,
            "Should the addon and all of it's featured be enabled?"
    );

    private final String name;
    protected Map<String, ConfigOptionType<?>> options;
    protected CommentedFileConfiguration config;
    protected File file;

    /**
     * Create a new instance of the addon config
     */
    public AddonConfig(String name) {
        this.name = name;
        this.options = new HashMap<>();
    }

    /**
     * Load the configuration for the addon
     */
    public void load() {
        this.register(ENABLED, "enabled");
        this.registerClass();
    }

    public void save() {
        if (this.file == null || this.config == null) return;

        this.options.forEach((path, type) -> type.write(this.config));
        this.config.save(this.file);
    }

    /**
     * Update a config option with a new type
     *
     * @param type The type to update
     */
    public void update(ConfigOptionType<?> type) {
        this.options.put(type.path(), type);
    }

    /**
     * Load this config inside the addon folder
     *
     * @param addonFolder The folder of the addon
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void reload(File addonFolder) {
        // Clear the options and config
        this.options = new HashMap<>();
        this.config = null;

        // Load the config values from the addon
        this.load();
        try {
            this.file = new File(addonFolder, this.name + ".yml");
            if (!this.file.exists()) {
                this.file.createNewFile();
            }
            
            // Load the config
            this.config = CommentedFileConfiguration.loadConfiguration(this.file);
            for (ConfigOptionType<?> option : this.options.values()) {
                if (option.path() == null) continue; // Ignore anything with no path 

                // If the config contains the path, cache the value
                if (this.config.contains(option.path())) {
                    option.read(this.config);
                    continue;
                }

                // Set the default value, this will also write any available 
                option.write(this.config);
            }

            this.config.save(this.file);
        } catch (Exception ex) {
            EssentialsPlugin.get().getLogger().severe("Failed to create config file for addon [" + name + "]: " + ex.getMessage());
        }
    }

    /**
     * Reload the plugin from the cached file
     */
    public void reload() {
        this.options.clear();
        this.config = null;
        this.load();
        this.save();
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
        String path = option.path() != null ? option.path() : def;
        if (path == null) {
            throw new IllegalArgumentException("Option [" + option + "] does not have a defined path");
        }

        if (this.options.containsKey(path)) {
            throw new IllegalArgumentException("Option with path " + path + " already exists");
        }

        option.path(path);
        this.options.put(path, option);
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
        return type -> {
            String typePath = type.path();
            return typePath == null || typePath.equalsIgnoreCase(path);
        };
    }

}
