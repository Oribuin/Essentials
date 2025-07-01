package dev.oribuin.essentials.addon.serverlist.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.Option;

import java.util.List;
import static dev.rosewood.rosegarden.config.SettingSerializers.*;

public class ServerListConfig extends AddonConfig {

    public final static Option<Integer> MAX_PLAYERS = new Option<>(INTEGER, 100, "The maximum amount of players that can be on the server at once.");
    public static final Option<List<String>> LINES = new Option<>(STRING_LIST, List.of("<rainbow>Welcome to the server!", "<white>This is a test message!"), "The lines that will be displayed on the server list.");
    public static final Option<List<String>> ICON_IMAGES = new Option<>(STRING_LIST, List.of("server-icon.png"), "The path to files to use as the server icon", "Requires a reboot to see changes", "All files will be loaded from /addons/serverlist/icons");

    /**
     * Create a new instance of the addon config
     */
    public ServerListConfig() {
        super("config");
    }

}
