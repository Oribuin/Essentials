package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class ClearCommand implements AddonCommand {

    /**
     * Clear a player's inventory
     *
     * @param sender The sender who is running the command
     */
    @Command("clearinventory|clear")
    @Permission("essentials.clear")
    @CommandDescription("Clear a player's current inventory")
    public void clear(Player sender) {
        BasicMessages messages = BasicMessages.get();
        PlayerInventory inventory = sender.getInventory();

        //        if (armour != null) {
        //            inventory.setContents(new ItemStack[0]);
        //        } else {
        inventory.setStorageContents(new ItemStack[0]);
        //        }

        messages.getClearInvSelf().send(sender, "target", sender.getName());
    }

    /**
     * Clear a player's inventory
     *
     * @param sender The sender who is running the command
     * @param target The target of the command
     */
    @Command("clearinventory|clear <target>")
    @Permission("essentials.clear.others")
    @CommandDescription("Clear a player's current inventory")
    public void clearOther(CommandSender sender, Player target) {
        BasicMessages messages = BasicMessages.get();
        PlayerInventory inventory = target.getInventory();

        //        if (armour != null) {
        //            inventory.setContents(new ItemStack[0]);
        //        } else {
        inventory.setStorageContents(new ItemStack[0]);
        //        }

        messages.getClearInvOther().send(sender, "target", target.getName());
    }

}
