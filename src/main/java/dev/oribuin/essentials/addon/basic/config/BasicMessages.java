package dev.oribuin.essentials.addon.basic.config;

import dev.oribuin.essentials.api.config.ConfigOption;
import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.MessageConfig;

public class BasicMessages extends MessageConfig {

    private static final String PREFIX = "<#bc7dff><b>Server</b> <gray>| <white>";

    // Ping Command
    public static final ConfigOption PING_SELF = new ConfigOption("ping-self", PREFIX + "Your ping is <#bc7dff>%ping%ms<white>.", "The message sent when a player checks their own ping.");
    public static final ConfigOption PING_OTHER = new ConfigOption("ping-other", PREFIX + "%player%'s ping is <#bc7dff>%ping%ms<white>.", "The message sent when a player checks another player's ping.");

}
