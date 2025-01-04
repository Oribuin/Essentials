package dev.oribuin.essentials;

import dev.oribuin.essentials.api.Module;
import dev.oribuin.essentials.api.config.ModuleConfig;
import dev.oribuin.essentials.manager.CommandManager;
import dev.oribuin.essentials.manager.DataManager;
import dev.oribuin.essentials.manager.LocaleManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.scheduler.RoseScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EssentialsPlugin extends RosePlugin {

    private static final Map<Class<? extends Module>, Module> modules = new ConcurrentHashMap<>();
    private static EssentialsPlugin instance;

    public EssentialsPlugin() {
        super(-1, -1,
                DataManager.class,
                LocaleManager.class,
                CommandManager.class
        );

        instance = this;
    }

    @Override
    public void enable() {
        Reflections reflections = new Reflections("xyz.oribuin.essentials.module", Scanners.SubTypes);
        Set<Class<? extends Module>> moduleClasses = reflections.getSubTypesOf(Module.class);

        // Register all the modules
        moduleClasses.forEach(aClass -> {
            if (Modifier.isAbstract(aClass.getModifiers())) return;

            try {
                modules.put(aClass, aClass.getConstructor(EssentialsPlugin.class).newInstance(this));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                this.getLogger().severe("Failed to create a new instance of the module: " + e.getMessage());
            }
        });

        modules.forEach((aClass, module) -> module.load());

        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    @Override
    public void reload() {
        super.reload();

        modules.forEach((aClass, module) -> module.disable());
        modules.forEach((aClass, module) -> module.load());

        // Update the commands for all online players
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    @Override
    public void disable() {
        modules.forEach((aClass, module) -> module.unload());
        modules.clear();
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of();
    }


    public static EssentialsPlugin get() {
        return instance;
    }

    public static RoseScheduler scheduler() {
        return RoseScheduler.getInstance(instance);
    }

    /**
     * Grab a module from the map
     *
     * @param clazz The class of the module
     * @param <T>   The module type
     *
     * @return The module
     */
    @SuppressWarnings("unchecked")
    public static <T extends Module> T getModule(Class<T> clazz) {
        if (modules.containsKey(clazz)) return (T) modules.get(clazz);

        try {
            T module = clazz.getConstructor(EssentialsPlugin.class).newInstance(EssentialsPlugin.get());
            module.load();
            modules.put(clazz, module);
            return module;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            EssentialsPlugin.get().getLogger().severe("Failed to create a new instance of the module: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get the configuration of a module from the map
     *
     * @param module The module to get the config from
     * @param config The config class
     * @param <T>    The config type
     *
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
            module.enabled(false);
            module.unload();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to unload the module: " + module.name() + " - " + e.getMessage());
        } finally {
            modules.remove(module.getClass());
        }
    }


}
