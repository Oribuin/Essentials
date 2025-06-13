package dev.oribuin.essentials.addon.serverlist.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.Option;

import java.util.List;

public class ServerListConfig extends AddonConfig {
    /**
     * Create a new instance of the addon config
     */
    public ServerListConfig() {
        super("config");
    }

    public final static Option<Integer> MAX_PLAYERS = new Option<>("max-players", 100, "The maximum amount of players that can be on the server at once.");
    public static final Option<List<String>> LINES = new Option<>("lines", List.of("Welcome to the server!", "This is a test message!"), "The lines that will be displayed on the server list.");

}
