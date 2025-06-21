package dev.oribuin.essentials.addon.spawn.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.Option;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import static dev.oribuin.essentials.api.config.EssentialsSerializers.LOCATION;
import static dev.rosewood.rosegarden.config.RoseSettingSerializers.BOOLEAN;

public class SpawnConfig extends AddonConfig {

    public static final Option<Boolean> USE_NEWBIE_SPAWN = new Option<>(BOOLEAN, false, "Should new players go to their own spawn point");
    public static final Option<Boolean> ALWAYS_SPAWN_ON_JOIN = new Option<>(BOOLEAN, false, 
            "When a player joins the server should they be forcefully",
            "forcefully teleported to the server spawn (This does not apply to new joins)"
    );
    public static final Option<Location> SPAWNPOINT = new Option<>(LOCATION, from("world", 0, 0, 0),
            "The default spawn point for all players when they join or type /spawn"
    );    
    public static final Option<Location> NEWBIE_SPAWN = new Option<>(LOCATION, from("world", 0, 0, 0),
            "The default spawn point for all players when they join or type /spawn"
    );

    /**
     * Create a new instance of the addon config
     */
    public SpawnConfig() {
        super("config");
    }

    /**
     * Convert a string and three doubles into a Location
     *
     * @param world The location wor ld
     * @param x     The x coordinate
     * @param y     The y coordinate
     * @param z     The z coordinate
     *
     * @return The world if available
     */
    private static Location from(String world, double x, double y, double z) {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
}
