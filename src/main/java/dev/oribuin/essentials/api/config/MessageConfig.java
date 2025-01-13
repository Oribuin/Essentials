package dev.oribuin.essentials.api.config;

public abstract class MessageConfig extends AddonConfig {
    
    /**
     * Create a new instance of the addon config
     */
    public MessageConfig() {
        super("messages");
    }

    /**
     * Load the configuration for the addon
     */
    @Override
    public void load() {
        this.registerClass();
    }
    
}
