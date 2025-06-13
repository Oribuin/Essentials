package dev.oribuin.essentials.addon.basic.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.Option;

import java.util.List;

public class BasicConfig extends AddonConfig {

    public final static Option<List<String>> DISABLED_FLIGHT_WORLDS = new Option<>(
            "disabled-flight-worlds",
            List.of("disabled-world-1"),
            "The world where /fly is disabled within"
    );

    /**
     * Create a new instance of the addon config
     */
    public BasicConfig() {
        super("config");
    }

}
