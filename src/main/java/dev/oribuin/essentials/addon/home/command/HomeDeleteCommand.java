package dev.oribuin.essentials.addon.home.command;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.home.command.argument.HomeArgumentHandler;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;

public class HomeDeleteCommand extends BaseRoseCommand {

    public HomeDeleteCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Home home) {
        Player sender = (Player) context.getSender();

        // Set the home
        AddonProvider.HOME_ADDON.repository().delete(home);
        HomeMessages.HOME_DELETED.send(sender, home.placeholders());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("delhome")
                .aliases("deletehome", "removehome", "unsethome")
                .arguments(ArgumentsDefinition.of("home", new HomeArgumentHandler()))
                .permission("essentials.home.delete")
                .playerOnly(true)
                .build();
    }

}
