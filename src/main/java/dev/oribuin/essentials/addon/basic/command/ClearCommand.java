package dev.oribuin.essentials.addon.basic.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClearCommand extends BaseRoseCommand {

    public ClearCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("trash")
                .permission("essentials.trash")
                .aliases("etrash", "bin", "disposal", "dispose", "edispose", "edisposal")
                .playerOnly(true)
                .build();
    }

}
