package dev.oribuin.essentials;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.hook.plugin.economy.PointsProvider;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.oribuin.essentials.manager.CommandManager;
import dev.oribuin.essentials.manager.DataManager;
import dev.oribuin.essentials.manager.LocaleManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.scheduler.RoseScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
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
        AddonProvider.register(this); // Register default addons for the plugin
        VaultProvider.get(); // Load the vault provider
        PointsProvider.get();  // Load the points provider
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands); // Update the commands for all online players
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
    public static <T extends Addon> T addon(Class<T> clazz) {
        if (addons.containsKey(clazz)) return (T) addons.get(clazz);

        return null;
    }

    /**
     * Get the configuration of a addon from the map
     *
     * @param addon  The addon to get the config from
     * @param config The config class
     * @param <T>    The config type
     *
     * @return The config
     */
    public static <T extends AddonConfig> T config(Class<? extends Addon> addon, Class<T> config) {
        Addon addonInstance = addons.get(addon);
        if (addonInstance == null) return null;

        return addonInstance.config(config);
    }

    /**
     * Register a new addon to the map
     *
     * @param addon The addon to register
     */
    public static void registerAddon(Addon addon) {
        try {
            addon.load(); // Load the addon
            addons.put(addon.getClass(), addon);
        } catch (Exception e) {
            EssentialsPlugin.get().getLogger().severe("Failed to register the addon: " + addon.getClass().getSimpleName() + " - " + e.getMessage());
        }
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
