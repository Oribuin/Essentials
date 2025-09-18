package dev.oribuin.essentials.addon.home.command;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.home.config.HomeConfig;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.oribuin.essentials.scheduler.task.ScheduledTask;
import dev.oribuin.essentials.util.EssUtils;
import dev.oribuin.essentials.util.StringPlaceholders;
import dev.oribuin.essentials.util.model.Confirmation;
import dev.oribuin.essentials.util.model.Cooldown;
import dev.oribuin.essentials.util.model.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.time.Duration;
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
public class HomeTPCommand implements AddonCommand {

    private final Cooldown<UUID> cooldown = new Cooldown<>();
    private final Confirmation<UUID> confirmation = new Confirmation<>(60, TimeUnit.SECONDS);

    /**
     * Teleport to your home
     *
     * @param sender The sender who is running the command
     * @param home   The target of the command
     */
    @Command("home <home>")
    @Permission("essentials.home.teleport")
    @CommandDescription("Teleport to your home")
    public void execute(Player sender, Home home) {
        HomeConfig config = HomeConfig.getInstance();
        HomeMessages messages = HomeMessages.getInstance();

        // Check if the world is disabled
        if (config.getDisabledWorlds().contains(home.location().getWorld().getName())) {
            messages.getDisabledWorld().send(sender);
            return;
        }

        // Number values are defaulted to 0 when not found
        Duration cooldown = config.getTeleportCooldown();
        Duration teleportDelay = config.getTeleportDelay();
        double cost = config.getTeleportCost();

        // establish all the placeholders
        StringPlaceholders placeholders = home.placeholders(Placeholders.of(
                "cost", cost,
                "delay", EssUtils.fromDuration(teleportDelay),
                "cooldown", EssUtils.fromDuration(cooldown)
        ));

        if (!home.isSafe() && !sender.hasPermission("essentials.home.bypass.unsafe")) {
            messages.getHomeUnsafe().send(sender, placeholders);
            return;
        }

        // Only run this if the cost is > 0 and bypass is enabled
        if (cost > 0.0 && !sender.hasPermission("essentials.home.bypass.cost")) {
            // check if they have enough
            if (!VaultProvider.get().has(sender, cost)) {
                messages.getInsufficientFunds().send(sender, placeholders);
                return;
            }
        }

        // Check if a player has confirmed they want to teleport here
        if (config.isTeleportConfirm() && !sender.hasPermission("essentials.home.bypass.confirm")) {
            if (!this.confirmation.passed(sender.getUniqueId())) {
                this.confirmation.apply(sender.getUniqueId());
                messages.getConfirmCommand().send(sender, placeholders);
                return;
            }
        }

        // Check if the player is on cooldown, ignore cooldown if they have a specific perm (disabled default)
        if (!cooldown.isZero() && !sender.hasPermission("essentials.home.bypass.cooldown")) {
            if (this.cooldown.onCooldown(sender.getUniqueId())) {
                long remaining = this.cooldown.getDurationRemaining(sender.getUniqueId()).getSeconds();
                messages.getHomeCooldown().send(sender, "time", remaining);
                return;
            }

            this.cooldown.setCooldown(sender.getUniqueId(), cooldown);
        }

        // player dosent need confirmation anymore
        this.confirmation.passed(sender.getUniqueId());

        // If the player has permission to bypass the delay, skip all effects
        if (sender.hasPermission("essentials.home.bypass.delay") || !teleportDelay.isZero()) {
            // send the final message
            messages.getHomeTeleporting().send(sender, placeholders);
            this.teleport(sender, home, cost, placeholders);
            return;
        }

        // Create the teleportation timer bar 
        ScheduledTask task = null;
        if (config.useTeleportBar()) {
            long start = System.currentTimeMillis();

            task = EssentialsPlugin.getInstance().getScheduler().runTaskTimerAsync(() -> sender.sendActionBar(
                    EssUtils.createTimerBar(teleportDelay.toMillis(), System.currentTimeMillis() - start)
            ), 0, 500, TimeUnit.MILLISECONDS);
        }

        // send the final message
        messages.getHomeTeleporting().send(sender, placeholders);

        // Teleport the player to the location
        ScheduledTask finalTask = task;
        EssentialsPlugin.getInstance().getScheduler().runTaskLater(() -> {
            if (finalTask != null) {
                finalTask.cancel();
                sender.sendActionBar(EssUtils.TIMER_FINISHED);
            }

            this.teleport(sender, home, cost, placeholders);
        }, teleportDelay.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Teleport to another player's home
     *
     * @param sender The sender who is running the command
     * @param home   The target of the command
     */
    @Command("home <home> <target>")
    @Permission("essentials.home.teleport")
    @CommandDescription("Teleport to your home")
    public void executeOther(Player sender, Home home, Player target) {
        HomeConfig config = HomeConfig.getInstance();
        HomeMessages messages = HomeMessages.getInstance();
        StringPlaceholders placeholders = Placeholders.builder()
                .addAll(home.placeholders())
                .add("owner", target.getName())
                .build();

        if (!home.isSafe() && !sender.hasPermission("essentials.home.bypass.unsafe")) {
            messages.getHomeUnsafe().send(sender, placeholders);
            return;
        }

        // send the final message
        messages.getHomeTeleportingOther().send(sender, placeholders);
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
        HomeMessages messages = HomeMessages.getInstance();

        player.teleportAsync(home.location(), PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(result -> {
            // check if teleport failed
            if (!result) {
                messages.getTeleportFailed().send(player, placeholders);
                return;
            }

            // Take away the money from the player.
            if (cost > 0) {
                VaultProvider.get().take(player, cost);
                messages.getTeleportFailed().send(player, placeholders);
            }
        });
    }


}
