package xyz.oribuin.essentials.module.teleport.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.essentials.Essentials;
import xyz.oribuin.essentials.module.teleport.TeleportModule;

public class TpaCommand extends BaseRoseCommand {

    public TpaCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        TeleportModule module = Essentials.getModule(TeleportModule.class);
        Player target = context.get("target");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("tpa")
                .aliases("tpask")
                .permission("essentials.command.tpa")
                .build();
    }


}
