package dev.oribuin.essentials.addon.spawn.command;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.spawn.config.SpawnConfig;
import dev.oribuin.essentials.addon.spawn.config.SpawnMessages;
import dev.oribuin.essentials.util.Confirmation;
import dev.oribuin.essentials.util.Placeholders;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.TimeUnit;

public class SpawnCommand extends BaseRoseCommand {

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

        // Check if a player has confirmed they want to teleport here
        if (SpawnConfig.TP_CONFIRM.value() && !sender.hasPermission("essentials.spawn.bypass.confirm")) {
            boolean check = this.confirmation.passed(sender);

            if (!check) {
                this.confirmation.apply(sender);
                SpawnMessages.CONFIRM_COMMAND.send(sender);
                return;
            }
        }

        // send the final message
        SpawnMessages.SPAWN_TELEPORT.send(sender);
        Location location = SpawnConfig.SPAWNPOINT.value().asLoc();
        if (SpawnConfig.TP_DELAY.value().isZero()) {
            this.teleport(sender, location, Placeholders.empty());
            return;
        }

        // Teleport the player to the location
        EssentialsPlugin.scheduler().runTaskLater(() -> this.teleport(
                sender,
                location,
                Placeholders.empty()
        ), SpawnConfig.TP_DELAY.value().toSeconds(), TimeUnit.SECONDS);
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
