package xyz.oribuin.essentials.api;


import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import xyz.oribuin.essentials.EssentialsPlugin;
import xyz.oribuin.essentials.api.config.ModuleConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class Module implements Listener {

    protected final Logger logger = Logger.getLogger("ess-" + this.name());
    protected final EssentialsPlugin plugin;
    protected File folder;
    private List<RoseCommandWrapper> commands = new ArrayList<>();

    /**
     * Create a new instance of the module
     *
     * @param plugin The plugin instance
     */
    public Module(EssentialsPlugin plugin) {
        this.plugin = plugin;
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

        boolean enabled = false;
        // Create and load the default configuration
        for (ModuleConfig config : this.configs()) {
            if (!enabled && config.get("enabled").asBoolean())
                enabled = true;

            config.reload(this.folder);
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
    }

    /**
     * The name of the module
     * This will be used for logging and the name of the module.
     */
    public abstract String name();

    /**
     * When the module is finished loading and is ready to be used.
     */
    public abstract void enable();

    /**
     * When the module is being disabled.
     */
    public abstract void disable();

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
        return new ArrayList<>();
    }

    /**
     * Get all the listeners for the module
     */
    public List<Listener> listeners() {
        return new ArrayList<>();
    }


}
