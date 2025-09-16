package dev.oribuin.essentials.addon.spawn.config;

import dev.oribuin.essentials.addon.spawn.SpawnAddon;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.util.model.FinePosition;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.time.Duration;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class SpawnConfig implements AddonConfig {

    public static SpawnConfig getInstance() {
        return SpawnAddon.getInstance().getConfigLoader().get(SpawnConfig.class);
    }

    @Comment("Whether the basic addon module is enabled")
    private boolean enabled = true;

    @Comment("Should a player be required to confirm they want to teleport to spawn?")
    private boolean teleportConfirm = false;

    @Comment("Should there be a cost to teleport to the server spawn?")
    private double teleportCost = 0.0;

    @Comment("Should the plugin display a bar counting until the player teleports? (Requires 'teleport-delay' to be more than 0)")
    private boolean teleportBar = true;

    @Comment("Should there be a delay until the player teleports")
    private Duration teleportDelay = Duration.ofSeconds(5);

    @Comment("Should there be a cooldown between each command usage?")
    private Duration teleportCooldown = Duration.ofSeconds(30);

    @Comment("Should the plugin send an motd when a player joins?")
    private boolean useMOTD = true;

    @Comment("Should new players go to their own spawn point?")
    private boolean useNewbieSpawn = false;

    @Comment("Should players be teleported to the spawn when they die?")
    private boolean spawnOnRespawn = false;

    @Comment("When a player joins the server, should they always teleport to the server spawn?")
    private boolean alwaysSpawnOnJoin = false;

    @Comment("The regular spawnpoint when using /spawn")
    private FinePosition spawnpoint = new FinePosition();

    @Comment("The newbie spawnpoint (Requires the 'use-newbie-spawn' to be set to true")
    private FinePosition newbieSpawnpoint = new FinePosition();

    /**
     * Check if the addon config is enabled
     *
     * @return True if the addon config is enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isTeleportConfirm() {
        return teleportConfirm;
    }

    public double getTeleportCost() {
        return teleportCost;
    }

    public boolean isTeleportBar() {
        return teleportBar;
    }

    public Duration getTeleportDelay() {
        return teleportDelay;
    }

    public Duration getTeleportCooldown() {
        return teleportCooldown;
    }

    public boolean useMotd() {
        return useMOTD;
    }

    public boolean isUseNewbieSpawn() {
        return useNewbieSpawn;
    }

    public boolean isSpawnOnRespawn() {
        return spawnOnRespawn;
    }

    public boolean isAlwaysSpawnOnJoin() {
        return alwaysSpawnOnJoin;
    }

    public FinePosition getSpawnpoint() {
        return spawnpoint;
    }

    public void setSpawnpoint(FinePosition spawnpoint) {
        this.spawnpoint = spawnpoint;
    }

    public FinePosition getNewbieSpawnpoint() {
        return newbieSpawnpoint;
    }

}
