package dev.oribuin.essentials.addon.serverlist;

import dev.oribuin.essentials.addon.serverlist.config.ServerListConfig;
import dev.oribuin.essentials.addon.serverlist.listener.ServerListListener;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
import org.bukkit.event.Listener;

import java.util.List;

public class ServerListAddon extends Addon {

    /**
     * The name of the addon
     * This will be used for logging and the name of the addon.
     */
    @Override
    public String name() {
        return "serverlist";
    }

    /**
     * Get all the listeners for the addon
     */
    @Override
    public List<Listener> listeners() {
        return List.of(new ServerListListener(this));
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public List<AddonConfig> configs() {
        return List.of(new ServerListConfig());
    }
}
