package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.BasicAddon;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class EnderchestCommand implements AddonCommand {

    private final BasicAddon addon;

    public EnderchestCommand(BasicAddon addon) {
        this.addon = addon;
    }

    /**
     * Open a player's current enderchest
     *
     * @param sender The sender who is running the command
     */
    @Command("enderchest|echest|ec")
    @Permission("essentials.gamemode")
    @CommandDescription("Change your current gamemode")
    public void execute(Player sender) {
        this.addon.getScheduler().runTask(() -> sender.openInventory(sender.getEnderChest()));
    }

    /**
     * Open a player's current enderchest
     *
     * @param sender The sender who is running the command
     * @param target The target of the command
     */
    @Command("enderchest|echest|ec <target>")
    @Permission("essentials.gamemode.others")
    @CommandDescription("Change your current gamemode")
    public void executeOther(Player sender, Player target) {
        this.addon.getScheduler().runTask(() -> sender.openInventory(target.getEnderChest()));
    }

}
