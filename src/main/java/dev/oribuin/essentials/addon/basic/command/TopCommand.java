package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.argument.FlagArgumentHandler;
import dev.oribuin.essentials.util.CommandFlag;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class TopCommand extends BaseRoseCommand {

    private static final CommandFlag<Boolean> CENTER = new CommandFlag<>("center");

    public TopCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, CommandFlag<Boolean> center) {
        Player player = (Player) context.getSender();

        World world = player.getWorld();
        Location current = player.getLocation();
        int maxHeight = current.getWorld().getMaxHeight();
        int height = current.getBlockY();
        Location newLocation = current.clone();

        // Start at world height 320 and count down (e.g. player coords are 100, 120, 100)
        // truthfully, getHighestBlockAt could do all this but i dont know and i dont feel like checking
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

        if (center != null) newLocation = newLocation.toCenterLocation();

        // Teleport the player to the top location
        player.teleport(newLocation);
        BasicMessages.TOP_COMMAND.send(player);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("top")
                .permission("essentials.top")
                .aliases("etop")
                .arguments(ArgumentsDefinition.of("center", new FlagArgumentHandler(CENTER)))
                .playerOnly(true)
                .build();
    }

}
