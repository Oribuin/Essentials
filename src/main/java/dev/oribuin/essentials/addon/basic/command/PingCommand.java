package dev.oribuin.essentials.addon.basic.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.basic.BasicAddon;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;

public class PingCommand extends BaseRoseCommand {

    public PingCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target) {
        BasicMessages messages = EssentialsPlugin.getConfig(BasicAddon.class, BasicMessages.class);
        if (messages == null) return;

        // Swap the target if the sender does not have permission to view other player's ping
        if (target != null && !context.getSender().hasPermission("essentials.ping.others") && context.getSender() instanceof Player sender) {
            target = sender;
        }

        // Send the ping message
        if (target != null) {
            BasicMessages.PING_OTHER.send(messages, context.getSender(), StringPlaceholders.of(
                    "ping", target.getPing(),
                    "player", target.getName()
            ));

            return;
        }

        // Send the ping message to the sender
        BasicMessages.PING_SELF.send(messages, context.getSender(), StringPlaceholders.of(
                "ping", context.getSender() instanceof Player player ? player.getPing() : 0
        ));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("ping")
                .arguments(ArgumentsDefinition.of("target", ArgumentHandlers.PLAYER))
                .build();
    }

}
