package dev.oribuin.essentials.addon.economy.config;

import dev.oribuin.essentials.api.config.AddonConfig;
import dev.oribuin.essentials.api.config.option.Option;

import java.util.List;

import static dev.rosewood.rosegarden.config.SettingSerializers.*;

public class EconomyConfig extends AddonConfig {

    public static Option<Double> STARTING_BALANCE = new Option<>(DOUBLE, 500.0, "The amount of money a player who has recently joined has.");
    public static Option<Integer> CACHE_DURATION = new Option<>(INTEGER, 30, "The number of seconds to hold a player's user account in the cache before being released");
    public static Option<String> CURRENCY_SYMBOL = new Option<>(STRING, "$", List.of("The symbol for all the currency displayed within the plugin"));
    public static Option<String> CURRENCY_NAME_PLURAL = new Option<>(STRING, "Coins", List.of("The name for plural economy balance>"));
    public static Option<String> CURRENCY_SEPARATOR = new Option<>(STRING, ",");
    public static Option<String> CURRENCY_DECIMAL = new Option<>(STRING, ".");
    public static Option<String> CURRENCY_ABBREV_THOUSANDS = new Option<>("abbreviations.thousands", STRING, "k");
    public static Option<String> CURRENCY_ABBREV_MILLIONS = new Option<>("abbreviations.millions", STRING, "m");
    public static Option<String> CURRENCY_ABBREV_BILLIONS = new Option<>("abbreviations.billions", STRING, "b");
    public static Option<Boolean> PAY_CONFIRM = new Option<>("confirm-payment-toggle", BOOLEAN, true);
    /**
     * Create a new instance of the addon config
     */
    public EconomyConfig() {
        super("config");
    }


}
