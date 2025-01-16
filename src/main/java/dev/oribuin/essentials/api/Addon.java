package dev.oribuin.essentials.api;


import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.api.config.DefaultConfig;
import dev.oribuin.essentials.api.config.AddonConfig;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.scheduler.RoseScheduler;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Addon implements Listener {

    protected final EssentialsPlugin plugin;
    protected final Logger logger = Logger.getLogger("ess-" + this.name());
    protected File folder;
    private final Map<Class<? extends AddonConfig>, AddonConfig> configs = new HashMap<>();
    private List<RoseCommandWrapper> commands;
    private boolean enabled;

    /**
     * Create a new instance of the addon
     *
     * @param plugin The plugin instance
     */
    public Addon(EssentialsPlugin plugin) {
        this.plugin = plugin;
        this.enabled = false;
        this.commands = new ArrayList<>();
    }

    /**
     * Load the configuration for the addon
     */
    public final void load() {

        // Create the default folder for the addon
        File addonsFolder = new File(this.plugin.getDataFolder(), "addons");
        if (!addonsFolder.exists()) addonsFolder.mkdirs();

        this.folder = new File(addonsFolder, this.name());
        if (!this.folder.exists()) this.folder.mkdirs();

        // Make sure the addon has configurations
        if (this.configs().isEmpty()) {
            this.logger.severe("The addon " + this.getClass().getSimpleName() + " has no configurations defined, This addon will be skipped.");
            return;
        }

        // Create and load the default configuration
        for (AddonConfig config : this.configs()) {
            config.reload(this.folder);

            // Register the config once loaded
            this.configs.put(config.getClass(), config);

            // Check if the addon is enabled
            if (!this.enabled) {
                this.enabled = config.getFile().getBoolean("enabled", false);
            }
        }

        // Don't register anything if the addon is disabled
        if (!enabled) {
            this.logger.warning("The addon is disabled, skipping loading.");
            EssentialsPlugin.unload(this);
            return;
        }

        // Register all the events
        this.listeners().add(this);
        this.listeners().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this.plugin));


        // Register all the commands
        this.commands = this.commands().stream().map(baseRoseCommand -> new RoseCommandWrapper(this.plugin, baseRoseCommand)).toList();
        this.commands.forEach(RoseCommandWrapper::register);
        this.enable();
    }

    /**
     * Unload the addon
     */
    public final void unload() {
        this.listeners().forEach(HandlerList::unregisterAll);
        this.commands.forEach(RoseCommandWrapper::unregister);
        this.commands.clear();
        this.configs.clear();

        this.disable();
    }

    /**
     * Get a configuration file from the addon
     *
     * @param clazz The class of the configuration file
     * @param <T>   The type of the configuration file
     *
     * @return The configuration file
     */
    @SuppressWarnings("unchecked")
    public <T extends AddonConfig> T config(Class<T> clazz) {
        if (!this.configs.containsKey(clazz))
            return null;

        return (T) this.configs.get(clazz);
    }

    /**
     * The name of the addon
     * This will be used for logging and the name of the addon.
     */
    public abstract String name();

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

    /**
     * Get all the commands for the addon
     */
    public List<BaseRoseCommand> commands() {
        return new ArrayList<>();
    }

    /**
     * Get all the configuration files for the addon
     */
    public List<AddonConfig> configs() {
        return new ArrayList<>(List.of(new DefaultConfig()));
    }

    /**
     * Get all the listeners for the addon
     */
    public List<Listener> listeners() {
        return new ArrayList<>();
    }

    /**
     * Check if the addon is enabled
     *
     * @return If the addon is enabled
     */
    public final boolean enabled() {
        return enabled;
    }

    /**
     * Set the addon to be enabled or disabled
     *
     * @param enabled If the addon is enabled
     */
    public final void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the logger for the addon
     *
     * @return The logger for the addon
     */
    public Logger logger() {
        return logger;
    }
    
    /**
     * Get the scheduler for the addon
     *
     * @return The scheduler for the addon
     */
    public RoseScheduler scheduler() {
        return EssentialsPlugin.scheduler();
    }
}
