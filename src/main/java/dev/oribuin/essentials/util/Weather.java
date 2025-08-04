package dev.oribuin.essentials.util;

import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;

// TODO: Add snowing condition
public enum Weather {
    RESET,
    CLEAR,
    RAIN,
    STORM;
    //    SNOW;

    /**
     * Check if the location is in the state of the weather
     *
     * @param location The location to check
     *
     * @return If the location is in the state of the weather
     */
    public boolean isState(Location location) {
        return this == test(location);
    }

    /**
     * Get the weather in the location provided
     *
     * @param location The location to check the weather
     *
     * @return The weather in the location
     */
    public static Weather test(Location location) {
        if (location == null || location.getWorld() == null) return Weather.CLEAR;

        World world = location.getWorld();
        if (!world.hasStorm()) return Weather.CLEAR;
        if (world.isThundering()) return Weather.STORM;

        return Weather.RAIN;
    }

    /**
     * Apply a weather state to the player provided
     *
     * @param player The player to change the weather for
     */
    public void apply(Player player) {
        if (player == null) return;

        // Reset the player's weather
        if (this == RESET) {
            player.resetPlayerWeather();
            return;
        }
        
        // Apply the specific weather 
        player.setPlayerWeather(this == CLEAR ? WeatherType.CLEAR : WeatherType.DOWNFALL);
    }

    /**
     * Apply a weather state to the location provided
     *
     * @param location The place to change the weather
     */
    public void apply(Location location, Integer duration) {
        if (location == null || location.getWorld() == null) return;

        World world = location.getWorld();
        int length = (duration != null ? duration : 0) * 20;

        // Clear all the current weather 
        if (this == CLEAR || this == RESET) {
            world.setStorm(false);
            world.setThundering(false);
            world.setClearWeatherDuration(length);
            return;
        }

        // Make it rain/thunderstorm
        world.setStorm(true); // setStorm = rain
        world.setThundering(this == STORM); // make it actually storm (add lightning)
        world.setWeatherDuration(length);
    }

}
