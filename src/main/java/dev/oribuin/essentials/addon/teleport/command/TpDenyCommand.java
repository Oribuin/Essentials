package dev.oribuin.essentials.addon.teleport.command;

import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.oribuin.essentials.addon.teleport.model.TeleportRequest;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.util.StringPlaceholders;
import dev.oribuin.essentials.util.model.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class TpDenyCommand implements AddonCommand {

    private final TeleportAddon addon;

    public TpDenyCommand(TeleportAddon addon) {
        this.addon = addon;
    }

    /**
     * Deny a teleport request from another player
     *
     * @param commandSender The sender who is running the command
     * @param target        The person to prioritise
     */
    @Command("tpdeny|tpadeny|tpno [target]")
    @Permission("essentials.tpdeny")
    @CommandDescription("Deny a teleport request from another player")
    public void execute(Player commandSender, Player target) {
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

        // important values
        StringPlaceholders placeholders = Placeholders.of(
                "target", target.getName(),
                "sender", target.getName()
        );

        addon.requests().remove(incoming);
        messages.getTeleportDenied().send(target, placeholders);
        messages.getTeleportDeniedOther().send(requestSender, placeholders);
    }

}
