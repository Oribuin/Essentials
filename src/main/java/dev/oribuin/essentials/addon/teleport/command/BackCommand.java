package dev.oribuin.essentials.addon.teleport.command;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BackCommand extends BaseRoseCommand {

    public BackCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        TeleportAddon addon = AddonProvider.TELEPORT_ADDON;
        Player sender = (Player) context.getSender();

        Location previous = addon.previousLocations().get(sender.getUniqueId());
        if (previous == null) {
            TeleportMessages.TELEPORT_BACK_INVALID.send(sender);
            return;
        }

        // Check if the player has access to teleport to the world
        if (!sender.hasPermission(addon.getPerm(previous.getWorld().getName()))) {
            TeleportMessages.DISABLED_WORLD.send(sender);
            return;
        }

        // TODO: Teleport Effects but who really cares
        sender.teleportAsync(previous).thenAccept(result -> {
            if (!result) {
                TeleportMessages.TELEPORT_FAILED.send(sender);
                return;
            }

            TeleportMessages.TELEPORT_BACK.send(sender);
        });
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("back")
                .permission("essentials.back")
                .playerOnly(true)
                .build();
    }

}
