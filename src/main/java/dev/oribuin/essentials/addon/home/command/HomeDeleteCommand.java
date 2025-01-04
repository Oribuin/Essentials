package dev.oribuin.essentials.addon.home.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.home.HomeAddon;
import dev.oribuin.essentials.command.argument.HomeArgumentHandler;
import dev.oribuin.essentials.addon.home.config.HomeConfig;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.model.Home;

import java.util.List;

public class HomeDeleteCommand extends BaseRoseCommand {

    public HomeDeleteCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player sender = (Player) context.getSender();
        Home home = context.get("home");

        HomeAddon addon = EssentialsPlugin.getModule(HomeAddon.class);
        if (addon == null || !addon.enabled()) return;

        HomeConfig config = addon.config(HomeConfig.class);
        HomeMessages msgConfig = addon.config(HomeMessages.class);
        if (config == null || msgConfig == null) return;

        // Check if the world is disabled
        List<String> disabledWorlds = HomeConfig.DISABLED_WORLDS.getOr(config, List.of()).asStringList();
        if (disabledWorlds.contains(home.location().getWorld().getName())) {
            HomeMessages.DISABLED_WORLD.send(msgConfig, sender);
            return;
        }

        // TODO: check if max homes reached
        // TODO: Check if home exists

        // Set the home
        addon.repository().save(home);
        HomeMessages.HOME_SET.send(msgConfig, sender, StringPlaceholders.of("home", home.name()));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("delhome")
                .aliases("deletehome", "removehome", "unsethome")
                .arguments(ArgumentsDefinition.of("home", new HomeArgumentHandler()))
                .permission("essentials.home")
                .playerOnly(true)
                .build();
    }

}
