package dev.oribuin.essentials.addon.home.command;

import dev.oribuin.essentials.addon.home.HomeAddon;
import dev.oribuin.essentials.addon.home.config.HomeConfig;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.database.HomeRepository;
import dev.oribuin.essentials.addon.home.event.HomeCreateEvent;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

public class HomeSetCommand {

    private final HomeAddon addon;

    public HomeSetCommand(HomeAddon addon) {
        this.addon = addon;
    }

    /**
     * Create a new user home
     *
     * @param sender The sender who is running the command
     * @param name   The name of the new home
     */
    @Command("sethome|createhome <home>")
    @Permission("essentials.home.create")
    @CommandDescription("Delete a user's home ")
    public void execute(Player sender, String name) {
        HomeConfig config = HomeConfig.getInstance();
        HomeMessages messages = HomeMessages.getInstance();
        HomeRepository repository = this.addon.repository();

        // Check if the world is disabled
        if (config.getDisabledWorlds().contains(sender.getWorld().getName())) {
            messages.getDisabledWorld().send(sender);
            return;
        }

        List<Home> current = repository.getHomes(sender.getUniqueId());

        // Check if a player has a home by that name already
        if (repository.checkExists(sender.getUniqueId(), name)) {
            messages.getHomeAlreadyExists().send(sender);
            return;
        }

        // Check the maximum homes a player can have
        int limit = HomeAddon.limit(sender);
        if (limit != -1 && current.size() >= limit) {
            messages.getHomeLimit().send(sender, "amt", current.size(), "limit", limit);
            return;
        }

        // Check for price of setting a home
        double setCost = config.getSetCost();
        if (setCost > 0 && !VaultProvider.get().has(sender, setCost)) {
            if (!VaultProvider.get().has(sender, setCost)) {
                messages.getInsufficientFunds().send(sender, "cost", setCost);
                return;
            }
        }

        Home home = new Home(name.toLowerCase(), sender.getUniqueId(), sender.getLocation().toCenterLocation());
        HomeCreateEvent event = new HomeCreateEvent(sender, home);
        event.callEvent();

        if (event.isCancelled()) return;

        // Set the home
        repository.save(home);
        messages.getHomeSet().send(sender, home.placeholders());
    }

}
