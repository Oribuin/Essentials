package dev.oribuin.essentials.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public interface AddonConfig {

    /**
     * Check if the addon config is enabled
     *
     * @return True if the addon config is enabled
     */
    default boolean isEnabled() {
        return true;
    }
}
