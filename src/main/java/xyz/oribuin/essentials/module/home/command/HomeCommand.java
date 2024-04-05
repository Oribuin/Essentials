package xyz.oribuin.essentials.module.home.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;

public class HomeCommand extends BaseRoseCommand {

    public HomeCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {

    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("home")
                .descriptionKey("command-home")
                .permission("essentials.command.home")
                .build();
    }

}
