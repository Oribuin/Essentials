package dev.oribuin.essentials;

import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
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

    private static final Map<Class<? extends Addon>, Addon> addons = new ConcurrentHashMap<>();
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
        Reflections reflections = new Reflections("xyz.oribuin.essentials.addon", Scanners.SubTypes);
        Set<Class<? extends Addon>> addonClasses = reflections.getSubTypesOf(Addon.class);

        // Register all the addons
        addonClasses.forEach(aClass -> {
            if (Modifier.isAbstract(aClass.getModifiers())) return;

            try {
                addons.put(aClass, aClass.getConstructor(EssentialsPlugin.class).newInstance(this));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                this.getLogger().severe("Failed to create a new instance of the addon: " + e.getMessage());
            }
        });

        addons.forEach((aClass, addon) -> addon.load());

        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    @Override
    public void reload() {
        super.reload();

        addons.forEach((aClass, addon) -> addon.disable());
        addons.forEach((aClass, addon) -> addon.load());

        // Update the commands for all online players
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    @Override
    public void disable() {
        addons.forEach((aClass, addon) -> addon.unload());
        addons.clear();
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
     * Grab a addon from the map
     *
     * @param clazz The class of the addon
     * @param <T>   The addon type
     *
     * @return The addon
     */
    @SuppressWarnings("unchecked")
    public static <T extends Addon> T getModule(Class<T> clazz) {
        if (addons.containsKey(clazz)) return (T) addons.get(clazz);

        try {
            T addon = clazz.getConstructor(EssentialsPlugin.class).newInstance(EssentialsPlugin.get());
            addon.load();
            addons.put(clazz, addon);
            return addon;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            EssentialsPlugin.get().getLogger().severe("Failed to create a new instance of the addon: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get the configuration of a addon from the map
     *
     * @param addon The addon to get the config from
     * @param config The config class
     * @param <T>    The config type
     *
     * @return The config
     */
    public static <T extends AddonConfig> T getConfig(Class<? extends Addon> addon, Class<T> config) {
        Addon addonInstance = addons.get(addon);
        if (addonInstance == null) return null;

        return addonInstance.config(config);
    }

    /**
     * Unload a specified addon from the map
     *
     * @param addon The addon to unload
     */
    public static void unload(Addon addon) {
        try {
            addon.enabled(false);
            addon.unload();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to unload the addon: " + addon.name() + " - " + e.getMessage());
        } finally {
            addons.remove(addon.getClass());
        }
    }


}
