package dev.oribuin.essentials.addon.basic.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.EssentialsSerializers;
import dev.oribuin.essentials.api.config.option.Option;
import dev.rosewood.rosegarden.config.SettingSerializers;

import java.time.Duration;
import java.util.List;

import static dev.oribuin.essentials.api.config.EssentialsSerializers.DURATION;

public class BasicConfig extends AddonConfig {

    public final static Option<List<String>> DISABLED_FLIGHT_WORLDS = new Option<>(
            SettingSerializers.STRING_LIST,
            List.of("disabled-world-1"),
            "The world where /fly is disabled within"
    );

    public final static Option<Duration> FEED_COOLDOWN = new Option<>(
            DURATION, Duration.ofMinutes(1),
            "The cooldown between each feed usage",
            "Set value to '0s' to disable the cooldown entirely."
    );

    public final static Option<Duration> HEAL_COOLDOWN = new Option<>(
            DURATION, Duration.ofMinutes(1),
            "The cooldown between each heal usage",
            "Set value to '0s' to disable the cooldown entirely."
    );
    
    public final static Option<Duration> REPAIR_COOLDOWN = new Option<>(
            DURATION, Duration.ofMinutes(1),
            "The cooldown between each repair usage",
            "Set value to '0s' to disable the cooldown entirely."
    );  
    
    /**
     * Create a new instance of the addon config
     */
    public BasicConfig() {
        super("config");
    }

}
