package dev.oribuin.essentials.addon.teleport.command;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.addon.spawn.config.SpawnConfig;
import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportConfig;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.oribuin.essentials.addon.teleport.model.TeleportRequest;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.oribuin.essentials.util.EssUtils;
import dev.oribuin.essentials.util.Placeholders;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TpAcceptCommand extends BaseRoseCommand {

    public TpAcceptCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target) {
        TeleportAddon addon = AddonProvider.TELEPORT_ADDON;
        Player commandSender = (Player) context.getSender(); // a bit confusing but target = person accepting the teleport request to them

        // Cancel the current outgoing teleport request
        TeleportRequest incoming = addon.getIncoming(commandSender.getUniqueId(), target);
        if (incoming == null) {
            TeleportMessages.TELEPORT_INVALID.send(commandSender);
            return;
        }

        // Check if the player has access to teleport to the world
        Player sender = Bukkit.getPlayer(incoming.sender());
        if (sender == null || !sender.isOnline()) {
            TeleportMessages.TELEPORT_INVALID.send(commandSender);
            return;
        }

        // Check if the sender is allowed to teleport to the player
        if (!sender.hasPermission(addon.getPerm(commandSender.getWorld().getName()))) {
            TeleportMessages.DISABLED_WORLD.send(commandSender);
            return;
        }

        // important values
        Duration teleportDelay = TeleportConfig.TP_DELAY.value();
        double cost = TeleportConfig.TP_COST.value();
        StringPlaceholders placeholders = Placeholders.of(
                "target", commandSender.getName(),
                "sender", sender.getName(),
                "cost", cost,
                "delay", EssUtils.fromDuration(teleportDelay)
        );

        addon.requests().remove(incoming);

        Location location = incoming.where() != null ? incoming.where() : commandSender.getLocation();

        // Check if the location is safe to teleport to TODO //  Implement FinePosition#isSafe or something to that effect
        if (!Home.isSafe(location) && !sender.hasPermission("essentials.tpa.bypass.unsafe")) {
            TeleportMessages.TELEPORT_UNSAFE.send(sender, placeholders);
            return;
        }

        // Only run this if the cost is > 0 and bypass is enabled
        if (cost > 0.0 && !sender.hasPermission("essentials.tpa.bypass.cost")) {
            // check if they have enough
            if (!VaultProvider.get().has(sender, cost)) {
                TeleportMessages.INSUFFICIENT_FUNDS.send(sender, placeholders);
                return;
            }
        }

        // If the player has permission to bypass the delay, skip all effects
        if (sender.hasPermission("essentials.tpa.bypass.delay") || teleportDelay.isZero()) {
            // send the final message
            TeleportMessages.TELEPORT_ACCEPT_SELF.send(commandSender, placeholders);
            TeleportMessages.TELEPORT_ACCEPT_OTHER.send(sender, placeholders);

            // TELEPORT EFFECTS WOOO!!!!!!!!!!!
            this.teleport(sender, location, cost, placeholders);
            return;
        }

        // Create the tp effects task
        ScheduledTask effectTask = null;
        if (SpawnConfig.TP_BAR.value()) {
            long start = System.currentTimeMillis();

            effectTask = EssentialsPlugin.scheduler().runTaskTimerAsync(() -> sender.sendActionBar(
                    EssUtils.createTimerBar(teleportDelay.toMillis(), System.currentTimeMillis() - start)
            ), 0, 500, TimeUnit.MILLISECONDS);
        }

        // send the final message
        TeleportMessages.TELEPORT_ACCEPT_SELF.send(commandSender, placeholders);
        TeleportMessages.TELEPORT_ACCEPT_OTHER.send(sender, placeholders);

        // Teleport the player to the location
        ScheduledTask finalTask = effectTask;
        EssentialsPlugin.scheduler().runTaskLater(() -> {
            if (finalTask != null) {
                finalTask.cancel();
                sender.sendActionBar(EssUtils.TIMER_FINISHED);
            }

            this.teleport(sender, location, cost, placeholders);
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
            // check if teleport failed
            if (!result) {
                TeleportMessages.TELEPORT_FAILED.send(player, placeholders);
                return;
            }

            // Take away the money from the player.
            if (cost > 0) {
                VaultProvider.get().take(player, cost);
                TeleportMessages.TELEPORT_COST.send(player, placeholders);
            }
        });
    }


    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("tpaccept")
                .aliases("tpyes")
                .permission("essentials.tpaccept")
                .arguments(
                        ArgumentsDefinition.builder()
                                .optional("target", ArgumentHandlers.PLAYER)
                                .build()
                )
                .playerOnly(true)
                .build();
    }

}
