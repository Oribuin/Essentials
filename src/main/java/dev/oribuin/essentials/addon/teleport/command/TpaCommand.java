package dev.oribuin.essentials.addon.teleport.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.teleport.TeleportAddon;

public class TpaCommand extends BaseRoseCommand {

    public TpaCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player target = context.get("target");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("tpa")
                .aliases("tpask")
                .permission("essentials.tpa")
                .build();
    }


}
