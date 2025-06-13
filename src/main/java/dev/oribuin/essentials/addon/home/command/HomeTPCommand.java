package dev.oribuin.essentials.addon.home.command;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.home.command.argument.HomeArgumentHandler;
import dev.oribuin.essentials.addon.home.config.HomeConfig;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Command Permissions:
 * - essentials.home.teleport - Use the command
 * - essentials.home.bypass.cost - Bypasses the teleport cost
 * - essentials.home.bypass.confirm - Bypasses the teleport confirm
 * - essentials.home.bypass.cooldown - Bypasses the required cooldown for the command
 * - essentials.home.bypass.delay - Bypasses the delays and effects
 * - essentials.home.bypass.unsafe - Bypasses the requirement for a home to be safe before teleporting
 */
public class HomeTPCommand extends BaseRoseCommand {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Cache<UUID, Home> confirmation = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    public HomeTPCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Home home) {
        Player sender = (Player) context.getSender();

        // Check if the world is disabled
        List<String> disabledWorlds = HomeConfig.DISABLED_WORLDS.getValue();
        if (disabledWorlds.contains(home.location().getWorld().getName())) {
            HomeMessages.DISABLED_WORLD.send(sender);
            return;
        }

        // Number values are defaulted to 0 when not found
        int cooldown = HomeConfig.TP_COOLDOWN.getValue();
        int teleportDelay = HomeConfig.TP_DELAY.getValue();
        double cost = HomeConfig.TP_COST.getValue();

        // establish all the placeholders
        StringPlaceholders placeholders = home.placeholders(StringPlaceholders.of(
                "cost", cost,
                "delay", teleportDelay,
                "cooldown", cooldown
        ));

        if (!home.isSafe() && !sender.hasPermission("essentials.home.bypass.unsafe")) {
            HomeMessages.HOME_UNSAFE.send(sender, placeholders);
            return;
        }

        // Only run this if the cost is > 0 and bypass is enabled
        if (cost > 0.0 && !sender.hasPermission("essentials.home.bypass.cost")) {
            // check if they have enough
            if (!VaultProvider.get().has(sender, cost)) {
                HomeMessages.INSUFFICIENT_FUNDS.send(sender, placeholders);
                return;
            }
        }

        // Check if a player has confirmed they want to teleport here
        if (HomeConfig.TP_CONFIRM.getValue() && !sender.hasPermission("essentials.home.bypass.confirm")) {
            Home confirmHome = this.confirmation.getIfPresent(sender.getUniqueId());

            if (confirmHome == null || !confirmHome.name().equalsIgnoreCase(home.name())) {
                this.confirmation.put(sender.getUniqueId(), home);
                HomeMessages.CONFIRM_COMMAND.send(sender, placeholders);
                return;
            }
        }

        // Check if the player is on cooldown, ignore cooldown if they have a specific perm (disabled default)
        if (cooldown > 0 && !sender.hasPermission("essentials.home.bypass.cooldown")) {
            long lastTeleport = this.cooldowns.getOrDefault(sender.getUniqueId(), 0L);
            long timeLeft = (lastTeleport + (cooldown * 1000L) - System.currentTimeMillis()) / 1000L;

            // Player is still on cooldown :3
            if (timeLeft > 0) {
                HomeMessages.HOME_COOLDOWN.send(sender, "time", timeLeft + "s");
                return;
            }

            this.cooldowns.put(sender.getUniqueId(), System.currentTimeMillis());
        }

        // player dosent need confirmation anymore
        this.confirmation.invalidate(sender.getUniqueId());

        // If the player has permission to bypass the delay, skip all effects
        if (sender.hasPermission("essentials.home.bypass.delay") || teleportDelay <= 0) {
            // send the final message
            HomeMessages.HOME_TELEPORT.send(sender, placeholders);
            this.teleport(sender, home, cost, placeholders);
            return;
        }

        // Create the tp effects task
        ScheduledTask effectTask = null;
        if (HomeConfig.TP_EFFECTS.getValue()) {
            // Give the player blindness
            sender.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                    (teleportDelay + 1) * 20, 4,
                    false,
                    false,
                    false
            ));

            ParticleBuilder particle = new ParticleBuilder(Particle.WITCH)
                    .count(10)
                    .offset(0.5, 0.5, 0.5)
                    .extra(0.1);

            effectTask = EssentialsPlugin.scheduler().runTaskTimerAsync(() ->
                            particle.location(sender.getLocation()).spawn(),
                    0, 250, TimeUnit.MILLISECONDS
            );
        }

        // send the final message
        HomeMessages.HOME_TELEPORT.send(sender, placeholders);

        // Teleport the player to the location
        ScheduledTask finalTask = effectTask;
        EssentialsPlugin.scheduler().runTaskLater(() -> {
            if (finalTask != null) finalTask.cancel();

            this.teleport(sender, home, cost, placeholders);
        }, teleportDelay, TimeUnit.SECONDS);
    }

    /**
     * Actually teleport the player to their home and run all the associated functionalities <3
     *
     * @param player       The player to teleport
     * @param home         The home to teleport to
     * @param cost         How much it cost to teleport the player
     * @param placeholders The placeholders for messages
     */
    private void teleport(Player player, Home home, double cost, StringPlaceholders placeholders) {
        player.teleportAsync(home.location(), PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(result -> {
            // check if teleport failed
            if (!result) {
                HomeMessages.TELEPORT_FAILED.send(player, placeholders);
                return;
            }

            // Take away the money from the player.
            if (cost > 0) {
                VaultProvider.get().take(player, cost);
                HomeMessages.TELEPORT_COST.send(player, placeholders);
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("home")
                .permission("essentials.home.teleport")
                .playerOnly(true)
                .arguments(this.createArgumentsDefinition())
                .build();
    }

    private ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("home", new HomeArgumentHandler())
                .optional("target", ArgumentHandlers.OFFLINE_PLAYER)
                .build();
    }

}
