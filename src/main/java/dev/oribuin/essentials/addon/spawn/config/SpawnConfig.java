package dev.oribuin.essentials.addon.spawn.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.Option;
import dev.oribuin.essentials.util.FinePosition;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.time.Duration;

import static dev.oribuin.essentials.api.config.EssentialsSerializers.DURATION;
import static dev.oribuin.essentials.api.config.EssentialsSerializers.POSITION;
import static dev.rosewood.rosegarden.config.SettingSerializers.BOOLEAN;

public class SpawnConfig extends AddonConfig {

    public static Option<Boolean> TP_CONFIRM = new Option<>(BOOLEAN, true, "Should a player be required to confirm they want to teleport to spawn?");
    public static Option<Duration> TP_DELAY = new Option<>(DURATION, Duration.ofSeconds(5), "The time it will take  for a player to teleport (Requires TP Effects)");
    public static final Option<Boolean> USE_MOTD = new Option<>(BOOLEAN, true, "Should a message be sent to players when the join the server?");
    public static final Option<Boolean> USE_NEWBIE_SPAWN = new Option<>(BOOLEAN, false, "Should new players go to their own spawn point");
    public static final Option<Boolean> SPAWN_ON_RESPAWN = new Option<>(BOOLEAN, false, "Should players be teleported to spawn when they die?");
    public static final Option<Boolean> ALWAYS_SPAWN_ON_JOIN = new Option<>(BOOLEAN, false,
            "When a player joins the server should they be forcefully",
            "forcefully teleported to the server spawn (This does not apply to new joins)"
    );
    public static final Option<FinePosition> SPAWNPOINT = new Option<>(POSITION, worldSpawn(),
            "The default spawn point for all players when they join or type /spawn"
    );
    public static final Option<FinePosition> NEWBIE_SPAWN = new Option<>(POSITION, worldSpawn(),
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
    private static FinePosition from(String world, double x, double y, double z) {
        return new FinePosition(world, x, y, z);
    }

    private static FinePosition worldSpawn() {
        World world = Bukkit.getWorlds().get(0);
        if (world == null) return from("world", 0, 65, 0);

        return FinePosition.from(world.getSpawnLocation().toCenterLocation());
    }
}
