package dev.oribuin.essentials.addon.home.command;

import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.home.HomeAddon;
import dev.oribuin.essentials.addon.home.config.HomeConfig;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.model.Home;

import java.util.List;

public class HomeSetCommand extends BaseRoseCommand {

    public HomeSetCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player sender = (Player) context.getSender();
        String name = context.get("name");

        HomeAddon addon = EssentialsPlugin.addon(HomeAddon.class);
        if (addon == null || !addon.enabled()) return;

        HomeConfig config = addon.config(HomeConfig.class);
        HomeMessages messages = addon.config(HomeMessages.class);
        if (config == null || messages == null) return;

        // Check if the world is disabled
        List<String> disabledWorlds = HomeConfig.DISABLED_WORLDS.getOr(config, List.of()).asStringList();
        if (disabledWorlds.contains(sender.getWorld().getName())) {
            HomeMessages.DISABLED_WORLD.send(sender);
            return;
        }

        List<Home> current = addon.repository().getHomes(sender.getUniqueId());

        // Check the maximum homes a player can have
        int limit = HomeAddon.limit(sender);
        if (limit != -1 && current.size() >= limit) {
            HomeMessages.MAX_HOMES.send(sender);
            return;
        }

        double setCost = HomeConfig.SET_COST.getOr(config, 0.0).asDouble();
        if (setCost > 0 && !VaultProvider.get().has(sender, setCost)) {

            // if (!VaultHook.has(sender, setCost)) {
            //     HomeMessages.INSUFFICIENT_FUNDS.send(msgConfig, sender);
            //     return;
            // }
        }

        // TODO: check if max homes reached
        // TODO: Check if home exists

        // Set the home
        Home home = new Home(name.toLowerCase(), sender.getUniqueId(), sender.getLocation().toCenterLocation());
        addon.repository().save(home);
        HomeMessages.HOME_SET.send(sender, home.placeholders());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("sethome")
                .aliases("createhome")
                .arguments(ArgumentsDefinition.of("name", ArgumentHandlers.STRING))
                .permission("essentials.home.create")
                .playerOnly(true)
                .build();
    }

}
