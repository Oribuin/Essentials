package dev.oribuin.essentials.addon.home.command;

import dev.oribuin.essentials.addon.home.HomeAddon;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.event.HomeDeleteEvent;
import dev.oribuin.essentials.addon.home.model.Home;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class HomeDeleteCommand {

    private final HomeAddon addon;

    public HomeDeleteCommand(HomeAddon addon) {
        this.addon = addon;
    }

    /**
     * Delete a user's home
     *
     * @param sender The sender who is running the command
     * @param home   The target of the command
     */
    @Command("delhome|removehome|unsethome <home>")
    @Permission("essentials.home.delete")
    @CommandDescription("Delete a user's home ")
    public void execute(Player sender, Home home) {
        HomeMessages messages = HomeMessages.getInstance();

        HomeDeleteEvent event = new HomeDeleteEvent(sender, home);
        event.callEvent();
        // Check if event was cancelled
        if (event.isCancelled()) return;

        // Set the home
        this.addon.repository().delete(home);
        messages.getHomeDeleted().send(sender, home.placeholders());
    }

    /**
     * Delete a user's home
     *
     * @param sender The sender who is running the command
     * @param home   The target of the command
     */
    @Command("delhome|removehome|unsethome <home> <target>")
    @Permission("essentials.home.delete.others")
    @CommandDescription("Add money to a users account")
    public void executeOther(Player sender, Home home, Player target) {
        HomeMessages messages = HomeMessages.getInstance();

        HomeDeleteEvent event = new HomeDeleteEvent(sender, home);
        event.callEvent();

        // Check if event was cancelled
        if (event.isCancelled()) return;

        // Delete the home
        this.addon.repository().delete(home);
        messages.getHomeDeleted().send(sender, home.placeholders());
    }

}
