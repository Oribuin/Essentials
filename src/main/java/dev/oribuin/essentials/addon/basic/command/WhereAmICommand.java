package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.BasicAddon;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class WhereAmICommand implements AddonCommand {

    private final BasicAddon addon;

    public WhereAmICommand(BasicAddon addon) {
        this.addon = addon;
    }

    /**
     * Gives the player their current location
     *
     * @param sender The sender who is running the command
     */
    @Command("whereami")
    @Permission("essentials.whereami")
    @CommandDescription("Gives the player their current location")
    public void execute(Player sender) {
        BasicMessages messages = BasicMessages.get();
        Location loc = sender.getLocation();

        messages.getWhereAmI().send(sender,
                "x", loc.getBlockX(),
                "y", loc.getBlockY(),
                "z", loc.getBlockZ(),
                "world", loc.getWorld().getName()
        );
    }

    /**
     * Gives the command sender a target's current location
     *
     * @param sender The sender who is running the command
     */
    @Command("whereis <target>")
    @Permission("essentials.whereis")
    @CommandDescription("Gives the command sender a target's current location")
    public void executeOther(CommandSender sender, Player target) {
        BasicMessages messages = BasicMessages.get();
        Location loc = target.getLocation();

        messages.getWhereIsOther().send(sender,
                "target", target.getName(),
                "x", loc.getBlockX(),
                "y", loc.getBlockY(),
                "z", loc.getBlockZ(),
                "world", loc.getWorld().getName()
        );
    }

}
