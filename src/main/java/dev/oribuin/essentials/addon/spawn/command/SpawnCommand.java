package dev.oribuin.essentials.addon.spawn.command;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.spawn.config.SpawnConfig;
import dev.oribuin.essentials.addon.spawn.config.SpawnMessages;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.oribuin.essentials.util.Confirmation;
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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpawnCommand extends BaseRoseCommand {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Confirmation<Player> confirmation = new Confirmation<>(30, TimeUnit.SECONDS, player -> {
        if (player == null || !player.isOnline()) return;

        SpawnMessages.CONFIRM_TIMEOUT.send(player);
    });

    public SpawnCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player sender = (Player) context.getSender();

        // Number values are defaulted to 0 when not found
        Duration cooldown = SpawnConfig.TP_COOLDOWN.value();
        Duration teleportDelay = SpawnConfig.TP_DELAY.value();
        double cost = SpawnConfig.TP_COST.value();

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
                SpawnMessages.INSUFFICIENT_FUNDS.send(sender, placeholders);
                return;
            }
        }

        // Check if a player has confirmed they want to teleport here
        if (SpawnConfig.TP_CONFIRM.value() && !sender.hasPermission("essentials.spawn.bypass.confirm")) {
            boolean check = this.confirmation.passed(sender);

            if (!check) {
                this.confirmation.apply(sender);
                SpawnMessages.CONFIRM_COMMAND.send(sender);
                return;
            }
        }

        // Check if the player is on cooldown, ignore cooldown if they have a specific perm (disabled default)
        if (!cooldown.isZero() && !sender.hasPermission("essentials.spawn.bypass.cooldown")) {
            long lastTeleport = this.cooldowns.getOrDefault(sender.getUniqueId(), 0L);
            long timeLeft = (lastTeleport + cooldown.toMillis() - System.currentTimeMillis()) / 1000L;

            // Player is still on cooldown :3
            if (timeLeft > 0) {
                SpawnMessages.TELEPORT_COOLDOWN.send(sender, "time", timeLeft + "s");
                return;
            }

            this.cooldowns.put(sender.getUniqueId(), System.currentTimeMillis());
        }

        // player dosent need confirmation anymore
        this.confirmation.passed(sender);

        // send the final message
        SpawnMessages.SPAWN_TELEPORT.send(sender);
        Location location = SpawnConfig.SPAWNPOINT.value().asLoc();
        if (SpawnConfig.TP_DELAY.value().isZero()) {
            this.teleport(sender, location, Placeholders.empty());
            return;
        }

        // Create the teleportation timer bar 
        ScheduledTask task = null;
        if (SpawnConfig.TP_BAR.value()) {
            long start = System.currentTimeMillis();

            task = EssentialsPlugin.scheduler().runTaskTimerAsync(() -> sender.sendActionBar(
                    EssUtils.createTimerBar(teleportDelay.toMillis(), System.currentTimeMillis() - start)
            ), 0, 500, TimeUnit.MILLISECONDS);
        }

        // Teleport the player to the location
        ScheduledTask finalTask = task;
        EssentialsPlugin.scheduler().runTaskLater(() -> {
            if (finalTask != null) {
                finalTask.cancel();
                sender.sendActionBar(EssUtils.TIMER_FINISHED);
            }
            this.teleport(sender, location, Placeholders.empty());
        }, teleportDelay.toSeconds(), TimeUnit.SECONDS);
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
                SpawnMessages.TELEPORT_FAILED.send(player, placeholders);
            }
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("spawn")
                .aliases("spawnpoint")
                .permission("essentials.spawn")
                .playerOnly(true)
                .build();
    }

    private ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .optional("target", ArgumentHandlers.OFFLINE_PLAYER)
                .build();
    }

}
