package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicConfig;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

public class FlyCommand implements AddonCommand {

    /**
     * Toggle a player's flight status
     *
     * @param sender The sender who is running the command
     */
    @Command("fly|efly|togglefly")
    @Permission("essentials.fly")
    @CommandDescription("Toggle your flight state")
    public void execute(Player sender) {
        BasicConfig config = BasicConfig.get();
        BasicMessages messages = BasicMessages.get();


        // Check if world is disabled
        List<String> disabledWorlds = config.getDisabledFlightWorlds();
        if (!disabledWorlds.isEmpty() && disabledWorlds.contains(sender.getWorld().getName())) {
            messages.getFlyDisabledWorld().send(sender);
            return;
        }

        String status = !sender.getAllowFlight() ? messages.getFlyEnabledShorthand() : messages.getFlyDisabledShorthand();
        sender.setAllowFlight(!sender.getAllowFlight());
        messages.getFlySelf().send(sender, "status", status);
    }

    /**
     * Toggle a player's flight status
     *
     * @param sender The sender who is running the command
     * @param target The target of the command
     */
    @Command("fly|efly|togglefly <target>")
    @Permission("essentials.fly.others")
    @CommandDescription("Toggle another player's flight state")
    public void executeOther(CommandSender sender, Player target) {
        BasicConfig config = BasicConfig.get();
        BasicMessages messages = BasicMessages.get();

        // Check if world is disabled
        List<String> disabledWorlds = config.getDisabledFlightWorlds();
        if (!disabledWorlds.isEmpty() && disabledWorlds.contains(target.getWorld().getName())) {
            messages.getFlyDisabledWorld().send(sender);
            return;
        }

        String status = !target.getAllowFlight() ? "Enabled" : "Disabled";
        target.setAllowFlight(!target.getAllowFlight());
        messages.getFlyOther().send(sender, "target", target.getName(), "status", status);
    }

}
