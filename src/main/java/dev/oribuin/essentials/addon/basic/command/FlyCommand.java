package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicConfig;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.util.EssUtils;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;

import java.util.List;

public class FlyCommand extends BaseRoseCommand {

    public FlyCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target) {
        // Swap the target if the sender does not have permission to view other player's ping
        if (target != null && !context.getSender().hasPermission("essentials.fly.others") && context.getSender() instanceof Player sender) {
            target = sender;
        }

        Player focus = target != null ? target : (Player) context.getSender();

        // Check if world is disabled
        List<String> disabledWorlds = BasicConfig.DISABLED_FLIGHT_WORLDS.getValue();
        if (!disabledWorlds.isEmpty() && disabledWorlds.contains(focus.getWorld().getName())) {
            BasicMessages.FLY_DISABLED_WORLD.send(context.getSender());
            return;
        }

        focus.setAllowFlight(!focus.getAllowFlight());
        String status = focus.getAllowFlight() ? "Enabled" : "Disabled";

        if (target != null) {
            BasicMessages.FLY_OTHER.send(context.getSender(),
                    "target", target.getName(),
                    "status", status
            );
        }

        BasicMessages.FLY_SELF.send(context.getSender(), "status", status);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("fly")
                .permission("essentials.fly")
                .aliases("flying", "enablefly")
                .arguments(EssUtils.createTarget(true))
                .build();
    }

}
