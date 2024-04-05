package xyz.oribuin.essentials.api.config;

import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ModuleConfig {

    private final String name;
    protected List<ConfigOption> options;
    protected CommentedFileConfiguration config;

    /**
     * Create a new instance of the module config
     *
     * @param name The name of the module
     */
    public ModuleConfig(String name) {
        this.name = name;
        this.options = new ArrayList<>();
    }

    /**
     * Load the configuration for the module
     */
    public abstract void load();

    /**
     * Load this config inside the module folder
     *
     * @param moduleFolder The folder of the module
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void reload(File moduleFolder) {
        // Clear the options and config
        this.options = new ArrayList<>();
        this.config = null;

        // Load the config values from the module
        this.load();

        try {
            File configFile = new File(moduleFolder, this.name + ".yml");
            if (!configFile.exists()) configFile.createNewFile();

            // Load the config
            this.config = CommentedFileConfiguration.loadConfiguration(configFile);
            for (ConfigOption option : this.options) {
                // Add comments to the config
                if (!option.getComments().isEmpty()) {
                    this.config.addPathedComments(option.getPath(), Arrays.toString(option.getComments().toArray()));
                }

                Object value = this.config.get(option.getPath(), option.getDefaultValue());
                option.setValue(value);
                this.config.set(option.getPath(), value);
            }
        } catch (IOException ex) {
            Bukkit.getLogger().severe("Failed to create config file for module " + this.name);
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
    public final ConfigOption get(String path) {
        return this.options.stream()
                .filter(option -> option.getPath().equalsIgnoreCase(path))
                .findFirst()
                .orElse(null);
    }


}
