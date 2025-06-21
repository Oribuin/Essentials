package dev.oribuin.essentials.addon.spawn;

import dev.oribuin.essentials.addon.spawn.config.SpawnConfig;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;

import java.util.List;

public class SpawnAddon extends Addon {
    
    /**
     * The name of the addon
     * This will be used for logging and the name of the addon.
     */
    @Override
    public String name() {
        return "spawn";
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public List<AddonConfig> configs() {
        return List.of(new SpawnConfig());
    }
}
