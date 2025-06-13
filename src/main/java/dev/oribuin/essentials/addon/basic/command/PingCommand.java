package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.util.EssUtils;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;

public class PingCommand extends BaseRoseCommand {

    public PingCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target) {
        // Swap the target if the sender does not have permission to view other player's ping
        if (target != null && !context.getSender().hasPermission("essentials.ping.others") && context.getSender() instanceof Player sender) {
            target = sender;
        }

        // Send the ping message
        if (target != null) {
            BasicMessages.PING_OTHER.send(context.getSender(),
                    "ping", target.getPing(),
                    "target", target.getName()
            );
            return;
        }

        // Send the ping message to the sender
        BasicMessages.PING_SELF.send(context.getSender(),
                "ping", context.getSender() instanceof Player player ? player.getPing() : 0
        );
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("ping")
                .permission("essentials.ping")
                .arguments(EssUtils.createTarget(true))
                .build();
    }

}
