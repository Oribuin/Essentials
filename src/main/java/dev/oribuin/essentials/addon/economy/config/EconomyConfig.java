package dev.oribuin.essentials.addon.economy.config;

import dev.oribuin.essentials.addon.economy.EconomyAddon;
import dev.oribuin.essentials.config.AddonConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class EconomyConfig implements AddonConfig {

    public static EconomyConfig get() {
        return EconomyAddon.getInstance().getConfigLoader().get(EconomyConfig.class);
    }

    @Comment("Whether the basic addon module is enabled")
    private boolean enabled = true;

    @Comment("The amount of money a player who has recently joined has.")
    private double startingBalance = 500.0;

    @Comment("The number of seconds to hold a player's account in the cache")
    private int cacheDuration = 30;

    @Comment("Should players have to confirm the money they're paying someone else")
    private boolean payConfirm = true;

    @Comment("The settings for all the currency language")
    private Currency currency = new Currency();

    @Comment("The settings for all abbreviations of currency amounts")
    private Abbreviations abbreviations = new Abbreviations();

    /**
     * Check if the addon config is enabled
     *
     * @return True if the addon config is enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @ConfigSerializable
    public static class Currency {
        @Comment("The symbol for all currency displayed within the plugin")
        private String symbol = "$";

        @Comment("The name for the plural amount of the economy balance")
        private String plural = "Coins";

        @Comment("The separator between every 3 digits of a balance")
        private String separator = ",";

        @Comment("The decimal symbol")
        private String decimal = ".";

        public String getSymbol() {
            return symbol;
        }

        public String getPlural() {
            return plural;
        }

        public String getSeparator() {
            return separator;
        }

        public String getDecimal() {
            return decimal;
        }
    }

    @ConfigSerializable
    public static class Abbreviations {
        private String thousands = "k";
        private String millions = "m";
        private String billion = "b";

        public String getThousands() {
            return thousands;
        }

        public String getMillions() {
            return millions;
        }

        public String getBillion() {
            return billion;
        }
    }

    public double getStartingBalance() {
        return startingBalance;
    }

    public int getCacheDuration() {
        return cacheDuration;
    }

    public boolean isPayConfirm() {
        return payConfirm;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Abbreviations getAbbreviations() {
        return abbreviations;
    }
}
