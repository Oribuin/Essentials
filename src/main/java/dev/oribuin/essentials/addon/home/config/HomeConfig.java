package dev.oribuin.essentials.addon.home.config;

import dev.oribuin.essentials.addon.home.HomeAddon;
import dev.oribuin.essentials.config.AddonConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.time.Duration;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class HomeConfig implements AddonConfig {

    public static HomeConfig getInstance() {
        return HomeAddon.getInstance().getConfigLoader().get(HomeConfig.class);
    }

    @Comment("Whether the basic addon module is enabled")
    private boolean enabled = true;

    @Comment("Homes cannot be set in these worlds.")
    private List<String> disabledWorlds = List.of("disabled_world");

    @Comment("The delay before teleporting to your home")
    private Duration teleportDelay = Duration.ofSeconds(5);

    @Comment("The cooldown before you can teleport to a home again")
    private Duration teleportCooldown = Duration.ofSeconds(30);

    @Comment("The cost to teleport to a home")
    private double teleportCost = 0.0;

    @Comment("Should the plugin display a bar counting until the player teleports (Reqiores teleport-delay to be above 0)")
    private boolean teleportBar = true;

    @Comment("Should a player be required to confirm they want to teleport to their home?")
    private boolean teleportConfirm = true;

    @Comment("The cost to create a new home")
    private double setCost = 0.0;

    /**
     * Check if the addon config is enabled
     *
     * @return True if the addon config is enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public Duration getTeleportDelay() {
        return teleportDelay;
    }

    public Duration getTeleportCooldown() {
        return teleportCooldown;
    }

    public double getTeleportCost() {
        return teleportCost;
    }

    public boolean useTeleportBar() {
        return teleportBar;
    }

    public boolean isTeleportConfirm() {
        return teleportConfirm;
    }

    public double getSetCost() {
        return setCost;
    }
}
