package dev.oribuin.essentials.addon.teleport.command;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.oribuin.essentials.addon.teleport.model.TeleportRequest;
import dev.oribuin.essentials.util.Placeholders;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpDenyCommand extends BaseRoseCommand {

    public TpDenyCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        TeleportAddon addon = AddonProvider.TELEPORT_ADDON;
        Player target = (Player) context.getSender(); // a bit confusing but target = person accepting the teleport request to them

        // Cancel the current outgoing teleport request
        TeleportRequest incoming = addon.getOutgoing(target.getUniqueId());
        if (incoming == null) {
            TeleportMessages.TELEPORT_INVALID.send(target);
            return;
        }

        // Check if the player has access to teleport to the world
        Player sender = Bukkit.getPlayer(incoming.sender());
        if (sender == null || !sender.isOnline()) {
            TeleportMessages.TELEPORT_INVALID.send(target);
            return;
        }

        // important values
        StringPlaceholders placeholders = Placeholders.of(
                "target", target.getName(),
                "sender", target.getName()
        );

        addon.requests().remove(incoming);
        TeleportMessages.TELEPORT_DENIED_SELF.send(target, placeholders);
        TeleportMessages.TELEPORT_DENIED_OTHER.send(sender, placeholders);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("tpdeny")
                .aliases("tpno", "tpadeny")
                .permission("essentials.tpdeny")
                .playerOnly(true)
                .build();
    }

}
