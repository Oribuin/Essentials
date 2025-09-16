package dev.oribuin.essentials.addon.teleport.command;

import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportConfig;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class BackCommand {

    private final TeleportAddon addon;

    public BackCommand(TeleportAddon addon) {
        this.addon = addon;
    }

    /**
     * Teleport back to your previous location
     *
     * @param sender The sender who is running the command
     */
    @Command("back")
    @Permission("essentials.back")
    @CommandDescription("Teleport back to your previous location")
    public void execute(Player sender) {
        TeleportMessages messages = TeleportMessages.getInstance();

        Location previous = addon.previousLocations().get(sender.getUniqueId());
        if (previous == null) {
            messages.getTeleportBackInvalid().send(sender);
            return;
        }

        // Check if the player has access to teleport to the world
        if (!sender.hasPermission(addon.getPerm(previous.getWorld().getName()))) {
            messages.getDisabledWorld().send(sender);
            return;
        }

        sender.teleportAsync(previous).thenAccept(result -> {
            if (!result) {
                messages.getTeleportFailed().send(sender);
                return;
            }

            messages.getTeleportBack().send(sender);
        });
    }

    /**
     * Teleport back to your previous location
     *
     * @param sender The sender who is running the command
     */
    @Command("back <target>")
    @Permission("essentials.back.others")
    @CommandDescription("Teleport back to your previous location")
    public void executeOther(CommandSender sender, Player target) {
        TeleportConfig config = TeleportConfig.getInstance();
        TeleportMessages messages = TeleportMessages.getInstance();

        Location previous = addon.previousLocations().get(target.getUniqueId());
        if (previous == null) {
            messages.getTeleportBackInvalidOther().send(sender);
            return;
        }

        // Check if the player has access to teleport to the world
        if (config.disableInaccessibleTp() && !sender.hasPermission(addon.getPerm(previous.getWorld().getName()))) {
            messages.getDisabledWorld().send(sender);
            return;
        }

        target.teleportAsync(previous).thenAccept(result -> {
            if (!result) {
                messages.getTeleportFailed().send(sender);
                return;
            }

            messages.getTeleportBackOther().send(sender);
        });
    }

}
