package dev.oribuin.essentials.addon.economy.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.Option;

import java.util.List;

public class EconomyConfig extends AddonConfig {

    public static Option<Double> STARTING_BALANCE = new Option<>(500.0, "The amount of money a player who has recently joined has.");
    public static Option<String> CURRENCY_SYMBOL = new Option<>("$", List.of("The symbol for all the currency displayed within the plugin"));
    public static Option<String> CURRENCY_NAME_PLURAL = new Option<>("Coins", List.of("The name for plural economy balance>"));
    public static Option<String> CURRENCY_SEPARATOR = new Option<>("currency-separator", ",", List.of());
    public static Option<String> CURRENCY_DECIMAL = new Option<>("currency-decimal", ".", List.of());
    public static Option<String> CURRENCY_ABBREV_THOUSANDS = new Option<>("abbreviations.thousands", "k", List.of());
    public static Option<String> CURRENCY_ABBREV_MILLIONS = new Option<>("abbreviations.millions", "m", List.of());
    public static Option<String> CURRENCY_ABBREV_BILLIONS = new Option<>("abbreviations.billions", "b", List.of());

    /**
     * Create a new instance of the addon config
     */
    public EconomyConfig() {
        super("config");
    }


}
