package dev.oribuin.essentials.module.basic.config;

import dev.oribuin.essentials.api.config.ModuleConfig;

public class BasicConfig extends ModuleConfig {

    /**
     * Create a new instance of the module config
     */
    public BasicConfig() {
        super("config");
    }

    /**
     * Load the configuration for the module
     */
    @Override
    public void load() {
        this.register(ModuleConfig.DEFAULT);
    }

}
