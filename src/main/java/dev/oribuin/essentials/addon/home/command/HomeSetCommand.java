package dev.oribuin.essentials.addon.home.command;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.home.HomeAddon;
import dev.oribuin.essentials.addon.home.config.HomeConfig;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.database.HomeRepository;
import dev.oribuin.essentials.addon.home.event.HomeCreateEvent;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeSetCommand extends BaseRoseCommand {
    
    private final HomeAddon addon;

    public HomeSetCommand(RosePlugin rosePlugin, HomeAddon addon) {
        super(rosePlugin);
        this.addon = addon;
    }

    @RoseExecutable
    public void execute(CommandContext context, String name) {
        Player sender = (Player) context.getSender();

        // Check if the world is disabled
        List<String> disabledWorlds = HomeConfig.DISABLED_WORLDS.value();
        if (disabledWorlds.contains(sender.getWorld().getName())) {
            this.addon.messages().from("disabled-world").send(sender);
            return;
        }

        HomeRepository repository = AddonProvider.HOME_ADDON.repository();
        List<Home> current = repository.getHomes(sender.getUniqueId());

        // Check if a player has a home by that name already
        if (repository.checkExists(sender.getUniqueId(), name)) {
            this.addon.messages().from("home-already-exists").send(sender);
            return;
        }

        // Check the maximum homes a player can have
        int limit = HomeAddon.limit(sender);
        if (limit != -1 && current.size() >= limit) {
            this.addon.messages().from("home-limit").send(sender, "amt", current.size(), "limit", limit);
            return;
        }

        // Check for price of setting a home
        double setCost = HomeConfig.SET_COST.value();
        if (setCost > 0 && !VaultProvider.get().has(sender, setCost)) {
            if (!VaultProvider.get().has(sender, setCost)) {
                this.addon.messages().from("insufficient-funds").send(sender, "cost", setCost);
                return;
            }
        }

        Home home = new Home(name.toLowerCase(), sender.getUniqueId(), sender.getLocation().toCenterLocation());
        HomeCreateEvent event = new HomeCreateEvent(sender, home);
        event.callEvent();

        if (event.isCancelled()) return;

        // Set the home
        repository.save(home);
        this.addon.messages().from("home-set").send(sender, home.placeholders());
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
