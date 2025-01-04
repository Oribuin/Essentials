package dev.oribuin.essentials.addon.basic.config;

import dev.oribuin.essentials.api.config.ConfigOption;
import dev.oribuin.essentials.api.config.AddonConfig;

public class BasicMessages extends AddonConfig {

    private static final String PREFIX = "<#bc7dff>&lServer &8| &f";

    // Ping Command
    public static final ConfigOption PING_SELF = new ConfigOption("ping-self", PREFIX + "Your ping is <#bc7dff>%ping%ms&f.", "The message sent when a player checks their own ping.");
    public static final ConfigOption PING_OTHER = new ConfigOption("ping-other", PREFIX + "%player%'s ping is <#bc7dff>%ping%ms&f.", "The message sent when a player checks another player's ping.");


    /**
     * Create a new instance of the addon config
     */
    public BasicMessages() {
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
