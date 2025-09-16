package dev.oribuin.essentials.addon.teleport.command;

import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.oribuin.essentials.addon.teleport.model.TeleportRequest;
import dev.oribuin.essentials.util.StringPlaceholders;
import dev.oribuin.essentials.util.model.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class TpCancelCommand {

    private final TeleportAddon addon;

    public TpCancelCommand(TeleportAddon addon) {
        this.addon = addon;
    }

    /**
     * Deny a teleport request from another player
     *
     * @param commandSender The sender who is running the command
     * @param target        The person to prioritise
     */
    @Command("tpcancel|tpundo")
    @Permission("essentials.tpcancel")
    @CommandDescription("Cancel a teleport request you sent to another player")
    public void execute(Player commandSender) {
        TeleportMessages messages = TeleportMessages.getInstance();

        // Cancel the current outgoing teleport request (Player sender = target, Player target = request sender)
        TeleportRequest outgoing = addon.getOutgoing(commandSender.getUniqueId());
        if (outgoing == null) {
            messages.getTeleportInvalid().send(commandSender);
            return;
        }

        addon.requests().remove(outgoing);
        messages.getTeleportCancelled().send(commandSender);
    }

}
