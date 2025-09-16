package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;

public class TopCommand {

    /**
     * Change your current player weather
     *
     * @param sender The sender who is running the command
     */
    @Command("top|etop")
    @Permission("essentials.top")
    @CommandDescription("Change your current player weather")
    public void execute(Player sender) {
        BasicMessages messages = BasicMessages.get();
        World world = sender.getWorld();
        Location current = sender.getLocation();
        int maxHeight = current.getWorld().getMaxHeight();
        int height = current.getBlockY();
        Location newLocation = current.clone();

        // Start at world height 320 and count down (e.g. player coords are 100, 120, 100)
        // truthfully, getHighestBlockAt could do all this but i don't know and i don't feel like checking
        for (int i = maxHeight; i > height; i--) {

            // get the block at the specific height above (e.g. 100, 320, 100)
            Block relative = world.getBlockAt(current.getBlockX(), i, current.getBlockZ());

            // make sure block is empty, this is going to be the block they're teleporting to
            if (!relative.getType().isAir()) continue;

            // Make sure the player won't die where they're standing 
            Block headHeight = relative.getRelative(BlockFace.UP);
            if (!headHeight.getType().isAir()) continue;

            // Make sure they're actually standing on a block
            Block standing = relative.getRelative(BlockFace.DOWN);
            if (standing.getType().isAir()) continue;

            current = relative.getLocation().clone();
        }

//        if (center != null) newLocation = newLocation.toCenterLocation();

        // Teleport the player to the top location
        sender.teleportAsync(newLocation);
        messages.getTopCommand().send(sender);
    }

}
