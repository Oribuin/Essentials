package xyz.oribuin.essentials.api.config;

public abstract class DefaultConfig extends ModuleConfig {

    /**
     * Create a new instance of the module config
     *
     * @param name The name of the module
     */
    public DefaultConfig(String name) {
        super(name);
    }

    /**
     * Load the configuration for the module
     */
    @Override
    public void load() {
        this.register(ConfigOption.enabled());
    }

}
