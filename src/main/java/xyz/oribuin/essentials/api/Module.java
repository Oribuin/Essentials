package xyz.oribuin.essentials.api;


import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import xyz.oribuin.essentials.Essentials;
import xyz.oribuin.essentials.api.config.DefaultConfig;
import xyz.oribuin.essentials.api.config.ModuleConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Module implements Listener {

    protected final Essentials plugin;
    protected final Logger logger = Logger.getLogger("ess-" + this.name());
    private Map<Class<? extends ModuleConfig>, ModuleConfig> configs = new HashMap<>();
    protected File folder;
    private List<RoseCommandWrapper> commands;
    private boolean enabled;

    /**
     * Create a new instance of the module
     *
     * @param plugin The plugin instance
     */
    public Module(Essentials plugin) {
        this.plugin = plugin;
        this.enabled = false;
        this.commands = new ArrayList<>();
    }

    /**
     * Load the configuration for the module
     */
    public final void load() {
        // Create the default folder for the module
        File modulesFolder = new File(this.plugin.getDataFolder(), "modules");
        if (!modulesFolder.exists()) modulesFolder.mkdirs();

        this.folder = new File(modulesFolder, this.name());
        if (!this.folder.exists()) this.folder.mkdirs();

        // Create and load the default configuration
        for (ModuleConfig config : this.configs()) {
            if (!this.enabled && config != null && config.get("enabled").asBoolean())
                this.enabled = true;

            config.reload(this.folder);
            this.configs.put(config.getClass(), config);
        }

        // Don't register anything if the module is disabled
        if (!enabled) return;

        // Register all the events
        this.listeners().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this.plugin));

        // Register all the commands
        this.commands = this.commands().stream().map(baseRoseCommand -> new RoseCommandWrapper(this.plugin, baseRoseCommand)).toList();
        this.commands.forEach(RoseCommandWrapper::register);

    }

    /**
     * Unload the module
     */
    public final void unload() {
        this.listeners().forEach(HandlerList::unregisterAll);

        this.commands.forEach(RoseCommandWrapper::unregister);
        this.commands.clear();
        this.configs.clear();
    }

    /**
     * Get a configuration file from the module
     *
     * @param clazz The class of the configuration file
     * @param <T>   The type of the configuration file
     * @return The configuration file
     */
    @SuppressWarnings("unchecked")
    public <T extends ModuleConfig> T config(Class<T> clazz) {
        if (!this.configs.containsKey(clazz))
            return null;

        return (T) this.configs.get(clazz);
    }

    /**
     * The name of the module
     * This will be used for logging and the name of the module.
     */
    public abstract String name();

    /**
     * When the module is finished loading and is ready to be used.
     */
    public void enable() {
        // Empty
    }

    /**
     * When the module is being disabled.
     */
    public void disable() {
        // Empty
    }

    /**
     * Get all the commands for the module
     */
    public List<BaseRoseCommand> commands() {
        return new ArrayList<>();
    }

    /**
     * Get all the configuration files for the module
     */
    public List<ModuleConfig> configs() {
        return new ArrayList<>(List.of(new DefaultConfig()));
    }

    /**
     * Get all the listeners for the module
     */
    public List<Listener> listeners() {
        return new ArrayList<>();
    }

    /**
     * Check if the module is enabled
     *
     * @return If the module is enabled
     */
    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the module to be enabled or disabled
     *
     * @param enabled If the module is enabled
     */
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the logger for the module
     *
     * @return The logger for the module
     */
    public Logger getLogger() {
        return logger;
    }

}
