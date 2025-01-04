package dev.oribuin.essentials.api.config;

public class DefaultConfig extends ModuleConfig {

    /**
     * Create a new instance of the module config
     */
    public DefaultConfig() {
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
