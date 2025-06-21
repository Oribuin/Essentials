package dev.oribuin.essentials.api.config;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.RoseSettingSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Utility Class for custom rose setting serializers
 */
public class EssentialsSerializers {

    public static final RoseSettingSerializer<Location> LOCATION = new RoseSettingSerializer<>() {
        @Override
        public Location read(CommentedConfigurationSection section, String key) {
            CommentedConfigurationSection locationSection = section.getConfigurationSection(key);
            if (locationSection == null) return null;
            String world = locationSection.getString("world", "world"); // default

            return new Location(
                    Bukkit.getWorld(world),
                    locationSection.getDouble("x"),
                    locationSection.getDouble("y"),
                    locationSection.getDouble("z")
            );
        }

        @Override
        public void write(CommentedConfigurationSection config, RoseSetting<Location> setting, Location value) {
            config.set("world", value.getWorld().getName());
            config.set("x", value.getX());
            config.set("y", value.getY());
            config.set("z", value.getZ());
        }
    };
}
