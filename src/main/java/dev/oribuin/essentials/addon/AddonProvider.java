package dev.oribuin.essentials.addon;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.basic.BasicAddon;
import dev.oribuin.essentials.addon.home.HomeAddon;
import dev.oribuin.essentials.addon.teleport.TeleportAddon;

public class AddonProvider {

    /**
     * Register all the addons for the plugin to use.
     *
     * @param plugin The plugin instance
     */
    public static void register(EssentialsPlugin plugin) {
        EssentialsPlugin.registerAddon(new BasicAddon(plugin));
        EssentialsPlugin.registerAddon(new HomeAddon(plugin));
        EssentialsPlugin.registerAddon(new TeleportAddon(plugin));
    }
}
