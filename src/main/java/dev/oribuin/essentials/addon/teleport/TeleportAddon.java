package dev.oribuin.essentials.addon.teleport;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.addon.teleport.config.TeleportConfig;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeleportAddon extends Addon {

    private Cache<UUID, UUID> requests = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build();
    
    /**
     * The name of the addon
     * This will be used for logging and the name of the addon.
     */
    @Override
    public String name() {
        return "teleport";
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public List<AddonConfig> configs() {
        return List.of(new TeleportConfig());
    }

}
