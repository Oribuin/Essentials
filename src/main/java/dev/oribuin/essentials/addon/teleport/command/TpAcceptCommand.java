package dev.oribuin.essentials.addon.teleport.command;

import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportConfig;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.oribuin.essentials.addon.teleport.model.TeleportRequest;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.oribuin.essentials.scheduler.task.ScheduledTask;
import dev.oribuin.essentials.util.EssUtils;
import dev.oribuin.essentials.util.StringPlaceholders;
import dev.oribuin.essentials.util.model.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TpAcceptCommand {

    private final TeleportAddon addon;

    public TpAcceptCommand(TeleportAddon addon) {
        this.addon = addon;
    }

    /**
     * Accept a teleport request from another player
     *
     * @param commandSender The sender who is running the command
     * @param target        The person to prioritise
     */
    @Command("tpaccept|tpyes [target]")
    @Permission("essentials.tpaccept")
    @CommandDescription("Accept a teleport request from another player")
    public void execute(Player commandSender, Player target) {
        TeleportConfig config = TeleportConfig.getInstance();
        TeleportMessages messages = TeleportMessages.getInstance();

        // Cancel the current outgoing teleport request (Player sender = target, Player target = request sender)
        TeleportRequest incoming = addon.getIncoming(commandSender.getUniqueId(), target);
        if (incoming == null) {
            messages.getTeleportInvalid().send(commandSender);
            return;
        }

        // Check if the player has access to teleport to the world
        Player requestSender = Bukkit.getPlayer(incoming.getSender());
        if (requestSender == null || !requestSender.isOnline()) {
            messages.getTeleportInvalid().send(commandSender);
            return;
        }

        // Check if the sender is allowed to teleport to the player
        if (config.disableInaccessibleTp() && !requestSender.hasPermission(addon.getPerm(commandSender.getWorld().getName()))) {
            messages.getDisabledWorld().send(commandSender);
            messages.getDisabledWorld().send(requestSender);
            return;
        }

        // important values
        Duration teleportDelay = config.getTeleportDelay();
        double cost = config.getTeleportCost();
        Location location = incoming.getWhere() != null ? incoming.getWhere() : commandSender.getLocation();
        StringPlaceholders placeholders = Placeholders.of(
                "target", commandSender.getName(),
                "sender", requestSender.getName(),
                "cost", cost,
                "delay", EssUtils.fromDuration(teleportDelay)
        );

        addon.requests().remove(incoming);
        
        // Check if the location is safe to teleport to TODO //  Implement FinePosition#isSafe or something to that effect
        if (!Home.isSafe(location) && !commandSender.hasPermission("essentials.tpa.bypass.unsafe")) {
            messages.getTeleportUnsafe().send(commandSender, placeholders);
            return;
        }

        // Only run this if the cost is > 0 and bypass is enabled
        if (cost > 0.0 && !commandSender.hasPermission("essentials.tpa.bypass.cost")) {
            // check if they have enough
            if (!VaultProvider.get().has(commandSender, cost)) {
                messages.getInsufficientFunds().send(commandSender, placeholders);
                return;
            }
        }

        // If the player has permission to bypass the delay, skip all effects
        if (commandSender.hasPermission("essentials.tpa.bypass.delay") || teleportDelay.isZero()) {
            // send the final message
            messages.getTeleportAccept().send(commandSender, placeholders);
            messages.getTeleportAcceptOther().send(requestSender, placeholders);

            // TELEPORT EFFECTS WOOO!!!!!!!!!!!
            this.teleport(requestSender, location, cost, placeholders);
            return;
        }

        // Create the tp effects task
        ScheduledTask effectTask = null;
        if (config.isTeleportBar()) {
            long start = System.currentTimeMillis();

            effectTask = this.addon.getScheduler().runTaskTimerAsync(() -> commandSender.sendActionBar(
                    EssUtils.createTimerBar(teleportDelay.toMillis(), System.currentTimeMillis() - start)
            ), 0, 500, TimeUnit.MILLISECONDS);
        }

        // send the final message
        messages.getTeleportAccept().send(commandSender, placeholders);
        messages.getTeleportAcceptOther().send(requestSender, placeholders);

        // Teleport the player to the location
        ScheduledTask finalTask = effectTask;
        this.addon.getScheduler().runTaskLater(() -> {
            if (finalTask != null) {
                finalTask.cancel();
                commandSender.sendActionBar(EssUtils.TIMER_FINISHED);
            }

            this.teleport(requestSender, location, cost, placeholders);
        }, teleportDelay.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Actually teleport the player to the other player and run all the associated functionalities <3
     *
     * @param player       The player to teleport
     * @param target       The target to teleport to
     * @param cost         How much it cost to teleport the player
     * @param placeholders The placeholders for messages
     */
    private void teleport(Player player, Location target, double cost, StringPlaceholders placeholders) {
        player.teleportAsync(target, PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(result -> {
            System.out.println("grrr");
            // check if teleport failed
            if (!result) {
                TeleportMessages.getInstance().getTeleportFailed().send(player, placeholders);
                return;
            }

            // Take away the money from the player.
            if (cost > 0) {
                VaultProvider.get().take(player, cost);
                TeleportMessages.getInstance().getTeleportCost().send(player, placeholders);
            }
            
        });
    }

}
