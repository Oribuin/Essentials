package xyz.oribuin.essentials.api.config;

import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ModuleConfig {

    public static final ConfigOption DEFAULT = new ConfigOption("enabled", false, List.of("Should the module and all of its featured be enabled?"));

    private final String name;
    protected List<ConfigOption> options;
    protected CommentedFileConfiguration config;

    /**
     * Create a new instance of the module config
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
     * Send a message from the config to a CommandSender
     *
     * @param sender       The CommandSender to send the message to
     * @param path         The path of the message in the config
     * @param placeholders The placeholders to apply to the message
     */
    public final void send(CommandSender sender, String path, StringPlaceholders placeholders) {
        String message = this.config.getString(path);
        if (message == null) return;

        String parsed = PlaceholderAPIHook.applyPlaceholders(sender instanceof Player player ? player : null, placeholders.apply(message));
        sender.sendMessage(HexUtils.colorify(parsed));
    }

    /**
     * Send a message from the config to a CommandSender using a config option
     *
     * @param sender The CommandSender to send the message to
     * @param option The option to send the message for
     */
    public final void send(CommandSender sender, ConfigOption option) {
        this.send(sender, option.getPath());
    }

    /**
     * Send a message from the config to a CommandSender using a config option
     *
     * @param sender       The CommandSender to send the message to
     * @param option       The option to send the message for
     * @param placeholders The placeholders to apply to the message
     */
    public final void send(CommandSender sender, ConfigOption option, StringPlaceholders placeholders) {
        this.send(sender, option.getPath(), placeholders);
    }

    /**
     * Send a message from the config to a CommandSender
     *
     * @param sender The CommandSender to send the message to
     * @param path   The path of the message in the config
     */
    public final void send(CommandSender sender, String path) {
        this.send(sender, path, StringPlaceholders.empty());
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
    public final ConfigOption get(String path) {
        if (this.config == null || path == null) return null;

        return this.options.stream()
                .filter(option -> option.getPath().equalsIgnoreCase(path))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a config option by its path
     *
     * @param option The config option to get
     * @return The config option
     */
    public final ConfigOption get(ConfigOption option) {
        if (this.config == null || option == null) return null;
        
        return this.options.stream()
                .filter(opt -> opt.getPath().equalsIgnoreCase(option.getPath()))
                .findFirst()
                .orElse(null);
    }


}
