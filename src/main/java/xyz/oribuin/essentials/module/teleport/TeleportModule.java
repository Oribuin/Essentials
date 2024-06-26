package xyz.oribuin.essentials.module.teleport;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import xyz.oribuin.essentials.Essentials;
import xyz.oribuin.essentials.api.Module;
import xyz.oribuin.essentials.api.config.ModuleConfig;
import xyz.oribuin.essentials.module.teleport.config.TeleportConfig;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeleportModule extends Module {

    private Cache<UUID, UUID> requests = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build();

    /**
     * Create a new instance of the module
     *
     * @param plugin The plugin instance
     */
    public TeleportModule(Essentials plugin) {
        super(plugin);
    }

    /**
     * The name of the module
     * This will be used for logging and the name of the module.
     */
    @Override
    public String name() {
        return "teleport";
    }

    /**
     * Get all the configuration files for the module
     */
    @Override
    public List<ModuleConfig> configs() {
        return List.of(new TeleportConfig());
    }

}
