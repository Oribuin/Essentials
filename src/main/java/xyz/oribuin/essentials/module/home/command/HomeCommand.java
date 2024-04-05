package xyz.oribuin.essentials.module.home.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.essentials.module.home.command.argument.HomeArgumentHandler;
import xyz.oribuin.essentials.module.home.model.Home;

public class HomeCommand extends BaseRoseCommand {

    public HomeCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Home home = context.get("home");

        if (context.getSender() instanceof Player player) {
            System.out.println("Teleporting player to home " + home.name() + " at " + home.location());
            home.teleport(player);
        }

    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("home")
                .descriptionKey("command-home")
                .permission("essentials.command.home")
                .playerOnly(true)
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("home", new HomeArgumentHandler())
                .required("target", ArgumentHandlers.OFFLINE_PLAYER)
                .build();
    }

}
