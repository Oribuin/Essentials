package dev.oribuin.essentials.addon.serverlist;

import dev.oribuin.essentials.addon.Addon;
import dev.oribuin.essentials.addon.serverlist.config.ServerListConfig;
import dev.oribuin.essentials.addon.serverlist.listener.ServerListListener;
import dev.oribuin.essentials.config.AddonConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ServerListAddon extends Addon {

    private static ServerListAddon instance;
    private final List<CachedServerIcon> icons = new ArrayList<>();

    /**
     * Create a new instance of the addon
     */
    public ServerListAddon() {
        super("serverlist");

        instance = this;
    }


    /**
     * When the addon is finished loading and is ready to be used.
     */
    @Override
    public void enable() {
        this.icons.clear();

        File folder = new File(this.folder, "icons");
        ServerListConfig.getInstance().getIconImages().forEach(name -> {
            File file = new File(folder, name);

            try {
                this.icons.add(Bukkit.loadServerIcon(file));
            } catch (Exception ex) {
            }
        });

        this.logger.info("Loaded a total of: " + this.icons.size() + " into the plugin");
    }


    /**
     * Get all the listeners for the addon
     */
    @Override
    public List<Listener> getListeners() {
        return List.of(new ServerListListener(this));
    }


    /**
     * Get all the configuration files for the addon
     */
    @Override
    public Map<String, Supplier<AddonConfig>> getConfigs() {
        return Map.of("config", ServerListConfig::new);
    }

    public List<CachedServerIcon> icons() {
        return icons;
    }

    public static ServerListAddon getInstance() {
        return instance;
    }
}
