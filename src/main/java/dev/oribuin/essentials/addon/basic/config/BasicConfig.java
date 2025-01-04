package dev.oribuin.essentials.addon.basic.config;

import dev.oribuin.essentials.api.config.AddonConfig;

public class BasicConfig extends AddonConfig {

    /**
     * Create a new instance of the addon config
     */
    public BasicConfig() {
        super("config");
    }

    /**
     * Load the configuration for the addon
     */
    @Override
    public void load() {
        this.register(AddonConfig.DEFAULT);
    }

}
