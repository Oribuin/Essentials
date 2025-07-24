package dev.oribuin.essentials.api.config;

import dev.oribuin.essentials.util.EssUtils;
import dev.oribuin.essentials.util.FinePosition;
import dev.rosewood.rosegarden.config.BaseSettingSerializer;
import dev.rosewood.rosegarden.config.SettingField;
import dev.rosewood.rosegarden.config.SettingSerializer;
import dev.rosewood.rosegarden.config.SettingSerializers;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;

import static dev.rosewood.rosegarden.config.SettingSerializers.setWithComments;

/**
 * Utility Class for custom rose setting serializers
 */
public class EssentialsSerializers {

    public static final SettingSerializer<FinePosition> POSITION = SettingSerializers.ofRecord(FinePosition.class, instance -> instance.group(
            SettingField.of("world", SettingSerializers.STRING, FinePosition::world),
            SettingField.of("x", SettingSerializers.DOUBLE, FinePosition::x),
            SettingField.of("y", SettingSerializers.DOUBLE, FinePosition::y),
            SettingField.of("z", SettingSerializers.DOUBLE, FinePosition::z),
            SettingField.of("yaw", SettingSerializers.FLOAT, FinePosition::yaw),
            SettingField.of("pitch", SettingSerializers.FLOAT, FinePosition::pitch)
    ).apply(instance, FinePosition::new));

    public static final SettingSerializer<Duration> DURATION = new BaseSettingSerializer<>(Duration.class) {
        public void write(ConfigurationSection config, String key, Duration value, String... comments) { setWithComments(config, key, EssUtils.fromDuration(value), comments); }
        public Duration read(ConfigurationSection config, String key) { return EssUtils.asDuration(config.getString(key)); }
    };
}
