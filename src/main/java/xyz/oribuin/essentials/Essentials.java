package xyz.oribuin.essentials;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import xyz.oribuin.essentials.api.Module;
import xyz.oribuin.essentials.api.config.ModuleConfig;
import xyz.oribuin.essentials.manager.ConfigurationManager;
import xyz.oribuin.essentials.manager.DataManager;
import xyz.oribuin.essentials.module.home.HomeModule;
import xyz.oribuin.essentials.module.teleport.TeleportModule;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Plugin Hex Code: #bc7dff
public class Essentials extends RosePlugin {

    private static final Map<Class<? extends Module>, Module> modules = new HashMap<>();
    private static Essentials instance;

    public Essentials() {
        super(-1, -1, ConfigurationManager.class, DataManager.class, null, null);

        instance = this;
    }

    @Override
    public void enable() {
        // TODO: Load all the modules
        modules.put(HomeModule.class, new HomeModule(this));
        modules.put(TeleportModule.class, new TeleportModule(this));

        modules.values().forEach(Module::load);
    }

    @Override
    public void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return new ArrayList<>();
    }

    public static Essentials get() {
        return instance;
    }

    /**
     * Grab a module from the map
     *
     * @param clazz The class of the module
     * @param <T>   The module type
     * @return The module
     */
    @SuppressWarnings("unchecked")
    public static <T extends Module> T getModule(Class<T> clazz) {
        if (modules.containsKey(clazz)) return (T) modules.get(clazz);

        try {
            T module = clazz.getConstructor(Essentials.class).newInstance(Essentials.get());
            module.load();
            modules.put(clazz, module);
            return module;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Bukkit.getLogger().severe("Failed to create a new instance of the module: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get the configuration of a module from the map
     *
     * @param module The module to get the config from
     * @param config The config class
     * @param <T>    The config type
     * @return The config
     */
    public static <T extends ModuleConfig> T getConfig(Class<? extends Module> module, Class<T> config) {
        Module moduleInstance = modules.get(module);
        if (moduleInstance == null) return null;

        return moduleInstance.config(config);
    }

    /**
     * Unload a specified module from the map
     *
     * @param module The module to unload
     */
    public static void unload(Module module) {
        try {
            module.setEnabled(false);
            module.disable();
            modules.remove(module.getClass());

        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to unload the module: " + module.name() + " - " + e.getMessage());
        }
    }

}
