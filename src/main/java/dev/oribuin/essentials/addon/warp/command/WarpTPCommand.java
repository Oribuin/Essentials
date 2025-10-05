package dev.oribuin.essentials.addon.warp.command;

import dev.oribuin.essentials.addon.warp.config.WarpConfig;
import dev.oribuin.essentials.addon.warp.config.WarpMessages;
import dev.oribuin.essentials.addon.warp.model.Warp;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.config.impl.PluginMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class WarpTPCommand implements AddonCommand {

    /**
     * Teleport to your warp
     *
     * @param sender The sender who is running the command
     * @param warp   The target of the command
     */
    @Command("warp <warp>")
    @Permission("essentials.warp")
    @CommandDescription("Teleport to your warp")
    public void execute(Player sender, Warp warp) {
        WarpConfig config = WarpConfig.getInstance();
        WarpMessages messages = WarpMessages.getInstance();

        // Check if the world is disabled
        if (config.getDisabledWorlds().contains(warp.location().getWorld().getName())) {
            messages.getDisabledWorld().send(sender);
            return;
        }

        if (!warp.canAccess(sender)) {
            PluginMessages.getInstance().getNoPermission().send(sender);
            return;
        }

        sender.teleportAsync(warp.location());
        messages.getWarpTeleporting().send(sender, "warp", warp.name());
    }

    /**
     * Force teleport a player to a warp
     *
     * @param sender The sender who is running the command
     * @param warp   The target of the command
     */
    @Command("warp <warp> <target>")
    @Permission("essentials.warp.others")
    @CommandDescription("Force teleport a player to a warp")
    public void executeOther(CommandSender sender, Warp warp, Player target) {
        WarpConfig config = WarpConfig.getInstance();
        WarpMessages messages = WarpMessages.getInstance();

        // Check if the world is disabled
        if (config.getDisabledWorlds().contains(warp.location().getWorld().getName())) {
            messages.getDisabledWorld().send(sender);
            return;
        }

        if (!warp.canAccess(sender)) {
            PluginMessages.getInstance().getNoPermission().send(sender);
            return;
        }

        target.teleportAsync(warp.location());
        messages.getWarpTeleporting().send(target, "warp", warp.name());
    }

}
