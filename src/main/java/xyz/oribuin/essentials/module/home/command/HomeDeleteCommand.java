package xyz.oribuin.essentials.module.home.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.essentials.Essentials;
import xyz.oribuin.essentials.module.home.HomeModule;
import xyz.oribuin.essentials.command.argument.HomeArgumentHandler;
import xyz.oribuin.essentials.module.home.config.HomeConfig;
import xyz.oribuin.essentials.module.home.config.HomeMessages;
import xyz.oribuin.essentials.module.home.model.Home;

import java.util.List;

public class HomeDeleteCommand extends BaseRoseCommand {

    public HomeDeleteCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player sender = (Player) context.getSender();
        Home home = context.get("home");

        HomeModule module = Essentials.getModule(HomeModule.class);
        if (module == null || !module.isEnabled()) return;

        HomeConfig config = module.config(HomeConfig.class);
        HomeMessages msgConfig = module.config(HomeMessages.class);
        if (config == null || msgConfig == null) return;

        // Check if the world is disabled
        List<String> disabledWorlds = HomeConfig.DISABLED_WORLDS.getOr(config, List.of()).asStringList();
        if (disabledWorlds.contains(home.location().getWorld().getName())) {
            HomeMessages.DISABLED_WORLD.send(msgConfig, sender);
            return;
        }

        double setCost = HomeConfig.SET_COST.getOr(config, 0.0).asDouble();
        if (setCost > 0) {
            // if (!VaultHook.has(sender, setCost)) {
            //     HomeMessages.INSUFFICIENT_FUNDS.send(msgConfig, sender);
            //     return;
            // }
        }

        // TODO: check if max homes reached
        // TODO: Check if home exists

        // Set the home
        module.getRepository().save(home);
        HomeMessages.HOME_SET.send(msgConfig, sender, StringPlaceholders.of("home", home.name()));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("delhome")
                .aliases("deletehome", "removehome", "unsethome")
                .permission("essentials.home")
                .playerOnly(true)
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("home", new HomeArgumentHandler())
                .build();
    }

}
