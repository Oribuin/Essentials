package dev.oribuin.essentials.api.config.type;

import dev.oribuin.essentials.api.config.AddonConfig;

public class DefaultConfig extends AddonConfig {

    /**
     * Create a new instance of the addon config
     */
    public DefaultConfig() {
        super("config");
    }

    /**
     * Load the configuration for the addon
     */
    @Override
    public void load() {
        this.register(AddonConfig.ENABLED, "enabled");
    }

}
