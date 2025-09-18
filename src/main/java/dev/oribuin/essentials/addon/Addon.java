package dev.oribuin.essentials.addon;


import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.config.ConfigHandler;
import dev.oribuin.essentials.config.ConfigLoader;
import dev.oribuin.essentials.scheduler.EssentialScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class Addon implements Listener {

    private static final File addonFolder = new File(EssentialsPlugin.getInstance().getDataFolder(), "addons");
    protected final Logger logger = Logger.getLogger("Essentials/" + this.getClass().getSimpleName());

    protected final EssentialsPlugin plugin;
    protected final String name;
    protected Map<Class<? extends AddonConfig>, ConfigHandler<?>> configs;
    protected ConfigLoader configLoader;
    protected File folder;
    protected boolean enabled;

    /**
     * Create a new instance of the addon
     *
     * @param name The name of the addon
     */
    public Addon(@NotNull String name) {
        this.plugin = EssentialsPlugin.getInstance();
        this.name = name;
        this.enabled = false;
        this.configs = new HashMap<>();
        this.folder = new File(addonFolder, this.name);
        this.configLoader = new ConfigLoader(this.folder.toPath());
    }

    /**
     * Load the configuration for the addon
     */
    public final void initialise() {

        // Create the default folder for the addon
        File addonsFolder = new File(this.plugin.getDataFolder(), "addons");
        if (!addonsFolder.exists()) addonsFolder.mkdirs();

        this.folder = new File(addonsFolder, this.name);
        if (!this.folder.exists()) this.folder.mkdirs();

        // Make sure the addon has configurations
        if (this.getConfigs().isEmpty()) {
            this.logger.severe("The addon " + this.getClass().getSimpleName() + " has no configurations defined, This addon will be skipped.");
            return;
        }

        this.load(); // Load the addon

        // Create and load the default configuration
        this.getConfigs().forEach((name, supplier) -> {
            AddonConfig config = supplier.get();
            if (config.getClass().getDeclaredAnnotation(ConfigSerializable.class) == null) {
                this.logger.warning("Config Class [" + config.getClass().getSimpleName() + "] is missing ConfigSerializable Annotation, This will be skipped.");
                return;
            }

            Class<? extends AddonConfig> configClass = config.getClass();
            this.configLoader.loadConfig(configClass, name);
            this.enabled = config.isEnabled();
        });

        // Don't register anything if the addon is disabled
        if (!this.enabled) {
            this.logger.warning("The addon is disabled, skipping loading.");
            AddonProvider.unload(this);
            return;
        }

        // Register all the events
        this.getListeners().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this.plugin));
        this.getPermissions().forEach(permission -> {
            PluginManager manager = this.plugin.getServer().getPluginManager();
            Permission loaded = manager.getPermission(permission.getName());
            if (loaded == null) {
                manager.addPermission(permission);
            }
        });

        // Register all the commands
        this.getCommands().forEach(addonCommand -> {
            try {
                this.plugin.getParser().parse(addonCommand);
            } catch (Exception ex) {
                this.logger.severe("There was an issue registering the command class [" + addonCommand.getClass().getSimpleName() + "]: " + ex.getMessage());
            }
        });

        this.enable();
    }

    /**
     * Unload the addon
     */
    public final void unload() {
        this.getListeners().forEach(HandlerList::unregisterAll);
        this.configLoader.close();
        this.configs.clear();
        this.disable();
    }

    /**
     * Register an argument parser into the plugin
     *
     * @param type   The object type that is being parsed
     * @param parser The argument parser being registered
     * @param <T>    The argument parser type
     * @param <C>    The object type
     */
    public final <T extends ArgumentParser<CommandSender, C>, C> void registerParser(Class<C> type, T parser) {
        ParserDescriptor<CommandSender, C> descriptor = ParserDescriptor.parserDescriptor(parser, type);
        this.plugin.getCommandManager().parserRegistry().registerParser(descriptor);
    }

    /**
     * Before the addon starts loading, register whatever is needed to be used when its enabled
     */
    public void load() {

    }

    /**
     * When the addon is finished loading and is ready to be used.
     */
    public void enable() {
        // Empty
    }

    /**
     * When the addon is being disabled.
     */
    public void disable() {
        // Empty
    }

    public EssentialsPlugin plugin() {
        return plugin;
    }

    /**
     * Get all the commands for the addon
     */
    public List<AddonCommand> getCommands() {
        return new ArrayList<>();
    }

    /**
     * Get all the configuration files for the addon
     */
    public Map<String, Supplier<AddonConfig>> getConfigs() {
        return new HashMap<>();
    }

    /**
     * Get all the listeners for the addon
     */
    public List<Listener> getListeners() {
        return new ArrayList<>();
    }

    public List<Permission> getPermissions() {
        return new ArrayList<>();
    }

    /**
     * Check if the addon is enabled
     *
     * @return If the addon is enabled
     */
    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the addon to be enabled or disabled
     *
     * @param enabled If the addon is enabled
     */
    public final void isEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the logger for the addon
     *
     * @return The logger for the addon
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Get the config loader for the addon
     *
     * @return The config loader
     */
    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    /**
     * Get the scheduler for the addon
     *
     * @return The scheduler for the addon
     */
    public EssentialScheduler getScheduler() {
        return EssentialsPlugin.getInstance().getScheduler();
    }

    public String getName() {
        return name;
    }
}
