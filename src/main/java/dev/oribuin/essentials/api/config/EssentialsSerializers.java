package dev.oribuin.essentials.api.config;

import dev.oribuin.essentials.util.FinePosition;
import dev.rosewood.rosegarden.config.SettingField;
import dev.rosewood.rosegarden.config.SettingSerializer;
import dev.rosewood.rosegarden.config.SettingSerializers;

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
}
