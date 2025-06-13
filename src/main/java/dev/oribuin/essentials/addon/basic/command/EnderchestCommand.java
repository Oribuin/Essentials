package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.util.EssUtils;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;

public class EnderchestCommand extends BaseRoseCommand {

    public EnderchestCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target) {
        if (!(context.getSender() instanceof Player commandSender)) return;

        // Swap the target if the sender does not have permission to view other player's ping
        if (target != null && !context.getSender().hasPermission("essentials.enderchest.others")) {
            target = commandSender;
        }

        // Send the ping message
        if (target != null) {
            commandSender.openInventory(target.getEnderChest());
            return;
        }

        // Send the ping message to the sender
        commandSender.openInventory(commandSender.getEnderChest());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("enderchest")
                .permission("essentials.enderchest")
                .arguments(EssUtils.createTarget(true))
                .aliases("ec", "echest")
                .playerOnly(true)
                .build();
    }

}
