package dev.oribuin.essentials.addon.serverlist;

import dev.oribuin.essentials.addon.serverlist.config.ServerListConfig;
import dev.oribuin.essentials.addon.serverlist.listener.ServerListListener;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerListAddon extends Addon {

    private final List<CachedServerIcon> icons = new ArrayList<>();

    /**
     * When the addon is finished loading and is ready to be used.
     */
    @Override
    public void enable() {
        this.icons.clear();

        ServerListConfig.FILES.getValue().forEach(s -> {
            File folder = new File(this.folder, "icons");
            File file = new File(folder, s);

            try {
                this.icons.add(Bukkit.loadServerIcon(file));
            } catch (Exception ex) {
                this.logger.warning("Failed to load icon: " + file.getPath() + " due to " + ex.getMessage());
            }
        });

        if (!this.icons.isEmpty()) {
            this.logger.info("Loaded a total of: " + this.icons.size() + " into the plugin");
        }
    }

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
        return List.of(new ServerListListener());
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public List<AddonConfig> configs() {
        return List.of(new ServerListConfig());
    }

    public List<CachedServerIcon> icons() {
        return icons;
    }
}
