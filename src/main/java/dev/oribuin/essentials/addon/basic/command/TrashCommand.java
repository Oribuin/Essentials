package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.BasicAddon;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class TrashCommand {
    
    private final BasicAddon addon;

    public TrashCommand(BasicAddon addon) {
        this.addon = addon;
    }

    /**
     * Open a trash can for the player to dispose of items
     *
     * @param sender The player opening the trashcan
     */
    @Command("trash|etrash|bin|disposal|dispose|edispose|edisposal")
    @Permission("essentials.trash")
    @CommandDescription("Open a trash can for the player")
    public void execute(Player sender) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 4, Component.text("Trash Can"));
        this.addon.getScheduler().runTask(() -> sender.openInventory(inventory));
    }
    
    /**
     * Open a trash can for the player to dispose of items
     *
     * @param sender The player opening the trashcan
     */
    @Command("trash|etrash|bin|disposal|dispose|edispose|edisposal <target>")
    @Permission("essentials.trash.other")
    @CommandDescription("Open a trash can for the player")
    public void execute(CommandSender sender, Player target) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 4, Component.text("Trash Can"));
        this.addon.getScheduler().runTask(() -> target.openInventory(inventory));
    }

}
