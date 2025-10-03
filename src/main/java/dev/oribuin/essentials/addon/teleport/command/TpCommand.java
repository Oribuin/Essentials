package dev.oribuin.essentials.addon.teleport.command;

import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.util.NumberUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class TpCommand implements AddonCommand {

    private final TeleportAddon addon;

    public TpCommand(TeleportAddon addon) {
        this.addon = addon;
    }

    /**
     * Force teleport yourself to a user
     *
     * @param sender The sender who is running the command
     * @param target The person to prioritise
     */
    @Command("teleport|tp <target>")
    @Permission("essentials.teleport")
    @CommandDescription("Force teleport yourself to a user")
    public void teleportSelf(Player sender, Player target) {
        TeleportMessages messages = TeleportMessages.getInstance();
        messages.getForceTeleportToOther().send(sender, "target", target.getName());
        sender.teleportAsync(target.getLocation());
    }

    /**
     * Force teleport yourself to a user
     *
     * @param sender The sender who is running the command
     * @param target The person to prioritise
     */
    @Command("teleport|tp <target> <destination>")
    @Permission("essentials.teleport")
    @CommandDescription("Force teleport yourself to a user")
    public void teleportOthers(CommandSender sender, Player target, Player destination) {
        TeleportMessages messages = TeleportMessages.getInstance();
        messages.getForceTeleportOthers().send(sender,
                "target", target.getName(),
                "destination", destination.getName()
        );
        target.teleportAsync(destination.getLocation());
    }

    /**
     * Force teleport a user to a different user
     *
     * @param sender The sender who is running the command
     * @param x      The x position to teleport to
     * @param y      The y position to teleport to
     * @param z      The z position to teleport to
     * @param world  The world to teleport to
     * @param yaw    The yaw position to teleport to
     * @param pitch  The pitch position to teleport to
     */
    @Command("teleportpos|tppos <x> <y> <z> [world] [yaw] [pitch]")
    @Permission("essentials.tppos")
    @CommandDescription("Force teleport yourself to a position")
    public void teleportPosition(Player sender, double x, double y, double z, World world, Float yaw, Float pitch) {
        TeleportMessages messages = TeleportMessages.getInstance();
        Location location = new Location(
                world != null ? world : sender.getWorld(),
                x, y, z,
                yaw != null ? yaw : sender.getYaw(),
                pitch != null ? pitch : sender.getPitch()
        );

        messages.getTeleportPosition().send(sender,
                "x", NumberUtil.rounded(location.getX()),
                "y", NumberUtil.rounded(location.getX()),
                "z", NumberUtil.rounded(location.getZ()),
                "world", location.getWorld().getName(),
                "yaw", NumberUtil.rounded(yaw),
                "pitch", NumberUtil.rounded(pitch)
        );
        
        sender.teleportAsync(location);
    }

}
