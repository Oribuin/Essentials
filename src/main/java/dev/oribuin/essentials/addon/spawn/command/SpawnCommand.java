package dev.oribuin.essentials.addon.spawn.command;

import dev.oribuin.essentials.addon.spawn.SpawnAddon;
import dev.oribuin.essentials.addon.spawn.config.SpawnConfig;
import dev.oribuin.essentials.addon.spawn.config.SpawnMessages;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.oribuin.essentials.scheduler.task.ScheduledTask;
import dev.oribuin.essentials.util.EssUtils;
import dev.oribuin.essentials.util.StringPlaceholders;
import dev.oribuin.essentials.util.model.Confirmation;
import dev.oribuin.essentials.util.model.Cooldown;
import dev.oribuin.essentials.util.model.Placeholders;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpawnCommand {

    private final SpawnAddon addon;
    private final Cooldown<UUID> cooldown;
    private final Confirmation<Player> confirmation;

    public SpawnCommand(SpawnAddon addon) {
        this.addon = addon;
        this.cooldown = new Cooldown<>();
        this.confirmation = new Confirmation<>(30, TimeUnit.SECONDS, player -> {
            if (player == null || !player.isOnline()) return;

            SpawnMessages.getInstance().getConfirmTimeout().send(player);
        });
    }

    /**
     * Teleport to the server spawn
     *
     * @param sender The sender who is running the command
     */
    @Command("spawn")
    @Permission("essentials.spawn")
    @CommandDescription("Teleport to the server spawn")
    public void execute(Player sender) {
        SpawnConfig config = SpawnConfig.getInstance();
        SpawnMessages messages = SpawnMessages.getInstance();

        // Number values are defaulted to 0 when not found
        Duration cooldown = config.getTeleportCooldown();
        Duration teleportDelay = config.getTeleportDelay();
        double cost = config.getTeleportCost();

        // establish all the placeholders
        StringPlaceholders placeholders = Placeholders.of(
                "cost", cost,
                "delay", EssUtils.fromDuration(teleportDelay),
                "cooldown", EssUtils.fromDuration(cooldown)
        );

        // Only run this if the cost is > 0 and bypass is enabled
        if (cost > 0.0 && !sender.hasPermission("essentials.spawn.bypass.cost")) {
            // check if they have enough
            if (!VaultProvider.get().has(sender, cost)) {
                messages.getInsufficientAmount().send(sender, placeholders);
                return;
            }
        }

        // Check if a player has confirmed they want to teleport here
        if (config.isTeleportConfirm() && !sender.hasPermission("essentials.spawn.bypass.confirm")) {
            boolean check = this.confirmation.passed(sender);

            if (!check) {
                this.confirmation.apply(sender);
                messages.getConfirmCommand().send(sender);
                return;
            }
        }

        // Check if the player is on cooldown, ignore cooldown if they have a specific perm (disabled default)
        if (!cooldown.isZero() && !sender.hasPermission("essentials.spawn.bypass.cooldown")) {

            if (this.cooldown.onCooldown(sender.getUniqueId())) {
                long remaining = this.cooldown.getDurationRemaining(sender.getUniqueId()).getSeconds();
                messages.getTeleportCooldown().send(sender, "time", remaining);
                return;
            }

            this.cooldown.setCooldown(sender.getUniqueId(), cooldown);
        }

        // player dosent need confirmation anymore
        this.confirmation.passed(sender);

        // send the final message
        messages.getSpawnTeleport().send(sender);
        Location location = config.getSpawnpoint().asLoc();

        // If there is a delay, cancel it
        if (config.getTeleportDelay().isZero()) {
            this.teleport(sender, location, Placeholders.empty());
            return;
        }

        // Create the teleportation timer bar 
        ScheduledTask task = null;
        if (config.isTeleportBar()) {
            long start = System.currentTimeMillis();

            task = this.addon.getScheduler().runTaskTimerAsync(() -> sender.sendActionBar(
                    EssUtils.createTimerBar(teleportDelay.toMillis(), System.currentTimeMillis() - start)
            ), 0, 500, TimeUnit.MILLISECONDS);
        }

        // Teleport the player to the location
        ScheduledTask finalTask = task;
        this.addon.getScheduler().runTaskLater(() -> {
            if (finalTask != null) {
                finalTask.cancel();
                sender.sendActionBar(EssUtils.TIMER_FINISHED);
            }
            this.teleport(sender, location, Placeholders.empty());
        }, teleportDelay.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Teleport to the server spawn
     *
     * @param sender The sender who is running the command
     */
    @Command("spawn <target>")
    @Permission("essentials.spawn.others")
    @CommandDescription("Teleport to the server spawn")
    public void executeOther(CommandSender sender, Player target) {
        SpawnConfig config = SpawnConfig.getInstance();
        SpawnMessages messages = SpawnMessages.getInstance();

        // send the final message
        Location location = config.getSpawnpoint().asLoc();
        messages.getSpawnTeleportOther().send(sender);
        this.teleport(target, location, Placeholders.empty());
    }

    /**
     * Actually teleport the player to the server spawn and run all the associated functionalities <3
     *
     * @param player       The player to teleport
     * @param location     The server spawn
     * @param placeholders The placeholders for messages
     */
    private void teleport(Player player, Location location, StringPlaceholders placeholders) {
        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(result -> {
            // check if teleport failed
            if (!result) {
                SpawnMessages.getInstance().getTeleportFailed().send(player, placeholders);
            }
        });
    }

}
