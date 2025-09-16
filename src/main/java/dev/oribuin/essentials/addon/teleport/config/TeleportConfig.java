package dev.oribuin.essentials.addon.teleport.config;

import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.config.AddonConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.time.Duration;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class TeleportConfig implements AddonConfig {

    public static TeleportConfig getInstance() {
        return TeleportAddon.getInstance().getConfigLoader().get(TeleportConfig.class);
    }

    @Comment("Whether the basic addon module is enabled")
    private boolean enabled = true;

    @Comment("Should players be prevented from teleporting into worlds they do not have access to (using permission essentials.teleport.<world>)")
    private boolean disableInaccessibleTeleport = false;

    @Comment("How long should a teleport request last")
    private Duration requestTimeout = Duration.ofSeconds(60);

    @Comment("Should there be a cost to teleport to the server spawn?")
    private double teleportCost = 0.0;

    @Comment("Should the plugin display a bar counting until the player teleports? (Requires 'teleport-delay' to be more than 0)")
    private boolean teleportBar = true;

    @Comment("Should there be a delay until the player teleports")
    private Duration teleportDelay = Duration.ofSeconds(5);

    @Comment("Should there be a cooldown between each command usage?")
    private Duration teleportCooldown = Duration.ofSeconds(30);

    /**
     * Check if the addon config is enabled
     *
     * @return True if the addon config is enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean disableInaccessibleTp() {
        return disableInaccessibleTeleport;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
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
}
