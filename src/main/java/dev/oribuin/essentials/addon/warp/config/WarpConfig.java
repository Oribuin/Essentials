package dev.oribuin.essentials.addon.warp.config;

import dev.oribuin.essentials.addon.warp.WarpsAddon;
import dev.oribuin.essentials.config.AddonConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.time.Duration;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class WarpConfig implements AddonConfig {

    public static WarpConfig getInstance() {
        return WarpsAddon.getInstance().getConfigLoader().get(WarpConfig.class);
    }

    @Comment("Whether the basic addon module is enabled")
    private boolean enabled = true;

    @Comment("Warps cannot be set in these worlds.")
    private List<String> disabledWorlds = List.of("disabled_world");


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
    
}
