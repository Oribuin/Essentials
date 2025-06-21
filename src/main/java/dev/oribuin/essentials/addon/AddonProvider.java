package dev.oribuin.essentials.addon;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.basic.BasicAddon;
import dev.oribuin.essentials.addon.economy.EconomyAddon;
import dev.oribuin.essentials.addon.home.HomeAddon;
import dev.oribuin.essentials.addon.serverlist.ServerListAddon;
import dev.oribuin.essentials.addon.spawn.SpawnAddon;
import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.ConfigOptionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class AddonProvider {

    private static final Map<Class<? extends Addon>, Addon> addons = new ConcurrentHashMap<>();
    public static final BasicAddon BASIC_ADDON = registerSupplier(BasicAddon::new);
    public static final EconomyAddon ECONOMY_ADDON = registerSupplier(EconomyAddon::new);
    public static final HomeAddon HOME_ADDON = registerSupplier(HomeAddon::new);
    public static final ServerListAddon SERVER_LIST_ADDON = registerSupplier(ServerListAddon::new);
    public static final SpawnAddon SPAWN_ADDON = registerSupplier(SpawnAddon::new);
    public static final TeleportAddon TELEPORT_ADDON = registerSupplier(TeleportAddon::new);

    public static void init() {
        EssentialsPlugin.get().getLogger().info("Initialised " + AddonProvider.class.getSimpleName() + " into the plugin.");
    }

    public static Map<Class<? extends Addon>, Addon> addons() {
        return addons;
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
     * Get an addon from the name of it
     *
     * @param name The name of the addon
     *
     * @return The addon if available
     */
    public static @Nullable Addon addon(@NotNull String name) {
        return addons.values().stream()
                .filter(x -> x.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Register a new addon to the map
     *
     * @param supplier The addon to register
     */
    public static <T extends Addon> T registerSupplier(Supplier<T> supplier) {
        return register(supplier.get());
    }

    /**
     * Register a new addon to the map
     *
     * @param addon The addon to register
     */
    public static <T extends Addon> T register(T addon) {
        EssentialsPlugin.get().getLogger().info("Registering Addon: " + addon.getClass().getSimpleName());

        try {
            addon.load(); // Load the addon
            addons.put(addon.getClass(), addon);
            return addon;
        } catch (Exception e) {
            EssentialsPlugin.get().getLogger().severe("Failed to register the addon: " + addon.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return null;
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
            EssentialsPlugin.get().getLogger().severe("Failed to unload the addon: " + addon.name() + " - " + e.getMessage());
        } finally {
            System.out.println("Removing from map");
            addons.remove(addon.getClass());
        }
    }


}
