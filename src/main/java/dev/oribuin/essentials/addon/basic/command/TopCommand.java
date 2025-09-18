package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class TopCommand implements AddonCommand {

    /**
     * Teleport to the highest block at your x/z coordinate
     *
     * @param sender The sender who is running the command
     */
    @Command("top|etop")
    @Permission("essentials.top")
    @CommandDescription("Teleport to the highest block at your x/z coordinate")
    public void execute(Player sender) {
        World world = sender.getWorld();
        Block highest = world.getHighestBlockAt(sender.getLocation());
        Location current = sender.getLocation().clone();
        double x = current.getX() - current.getBlockX();
        double z = current.getZ() - current.getBlockZ();

        // Teleport the player to the top location
        Location location = highest.getLocation().clone().add(x, 1, z);
        location.setYaw(sender.getYaw());
        location.setPitch(sender.getPitch());
        sender.teleportAsync(location);

        BasicMessages.get().getTopCommand().send(sender);
    }

    /**
     * Teleport to the highest block at your x/z coordinate
     *
     * @param sender The sender who is running the command
     */
    @Command("top|etop center")
    @Permission("essentials.top")
    @CommandDescription("Teleport to the highest block at your x/z coordinate")
    public void executeCenter(Player sender) {
        World world = sender.getWorld();
        Block highest = world.getHighestBlockAt(sender.getLocation());

        // Teleport the player to the top location
        Location location = highest.getLocation().clone().add(0.5, 1, 0.5);
        location.setYaw(sender.getYaw());
        location.setPitch(sender.getPitch());
        sender.teleportAsync(location);

        BasicMessages.get().getTopCommand().send(sender);
    }

}
