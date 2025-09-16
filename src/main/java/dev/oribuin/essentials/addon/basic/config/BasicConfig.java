package dev.oribuin.essentials.addon.basic.config;

import dev.oribuin.essentials.addon.basic.BasicAddon;
import dev.oribuin.essentials.config.AddonConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.time.Duration;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class BasicConfig implements AddonConfig {

    public static BasicConfig get() {
        return BasicAddon.get().getConfigLoader().get(BasicConfig.class);
    }

    @Comment("Whether the basic addon module is enabled")
    private boolean enabled = true;

    @Comment("The worlds where /fly is disabled within")
    private List<String> disabledFlightWorlds = List.of("disabled-world-1");

    @Comment("The cooldown between each /feed usage. Set value to '0s' to disable entirely")
    private Duration feedCooldown = Duration.ofMinutes(1);

    @Comment("The cooldown between each /heal usage. Set value to '0s' to disable entirely")
    private Duration healCooldown = Duration.ofMinutes(1);

    @Comment("The cooldown between each /repair usage. Set value to '0s' to disable entirely")
    private Duration repairCooldown = Duration.ofMinutes(1);

    @Comment("Should players be required to type the command again to clear their inventory?")
    private boolean requireClearConfirmation = true;

    /**
     * Check if the addon config is enabled
     *
     * @return True if the addon config is enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public List<String> getDisabledFlightWorlds() {
        return disabledFlightWorlds;
    }

    public Duration getFeedCooldown() {
        return feedCooldown;
    }

    public Duration getHealCooldown() {
        return healCooldown;
    }

    public Duration getRepairCooldown() {
        return repairCooldown;
    }

    public boolean isRequireClearConfirmation() {
        return requireClearConfirmation;
    }

}
