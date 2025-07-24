package dev.oribuin.essentials.addon.teleport.command;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportConfig;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.oribuin.essentials.addon.teleport.model.TeleportRequest;
import dev.oribuin.essentials.util.EssUtils;
import dev.oribuin.essentials.util.Placeholders;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class TpAskHereCommand extends BaseRoseCommand {

    public TpAskHereCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target) {
        TeleportAddon addon = AddonProvider.TELEPORT_ADDON;
        Player sender = (Player) context.getSender();

        // Check if the player has access to teleport to the world
        if (!sender.hasPermission(addon.getPerm(target.getWorld().getName()))) {
            TeleportMessages.DISABLED_WORLD.send(sender);
            return;
        }

        // Check if sending to self
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            TeleportMessages.TELEPORT_SELF.send(sender);
            return;
        }

        // Cancel the current outgoing teleport request
        TeleportRequest outgoing = addon.getOutgoing(sender.getUniqueId());
        if (outgoing != null) {
            addon.requests().remove(outgoing);
        }

        TeleportRequest request = new TeleportRequest(
                sender.getUniqueId(),
                target.getUniqueId(),
                System.currentTimeMillis(),
                sender.getLocation()
        );

        addon.requests().add(request);
        StringPlaceholders placeholders = Placeholders.of("target", target.getName(), "sender", sender.getName());
        TeleportMessages.TELEPORT_ASK_RECEIVED.send(target, placeholders);
        TeleportMessages.TELEPORT_ASK_SENT.send(sender, placeholders);

        // Send the timeout message after several seconds
        EssentialsPlugin.scheduler().runTaskLater(() -> {
            if (addon.requests().remove(request)) {
                TeleportMessages.TELEPORT_TIMEOUT.send(sender, placeholders);
                TeleportMessages.TELEPORT_TIMEOUT_OTHER.send(target, placeholders);
            }
        }, TeleportConfig.REQUEST_TIMEOUT.value(), TimeUnit.SECONDS);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("tpahere")
                .permission("essentials.tpahere")
                .arguments(EssUtils.createTarget(false))
                .playerOnly(true)
                .build();
    }

}
