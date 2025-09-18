package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.config.TextMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class PingCommand implements AddonCommand {

    /**
     * Check a players current latency to the server
     *
     * @param sender The sender who is running the command
     * @param target The target of the command
     */
    @Command("ping|eping|latency [target]")
    @Permission("essentials.ping")
    @CommandDescription("Check your current latency to the server")
    public void executeRegular(CommandSender sender, Player target) {
        BasicMessages messages = BasicMessages.get();

        // Check if the sender is allowed to control others
        if (target != null && !sender.hasPermission("essentials.ping.others")) {
            EssentialsPlugin.getMessages().getNoPermission().send(sender);
            return;
        }

        // Check if the sender is a player
        if (target == null && !(sender instanceof Player)) {
            EssentialsPlugin.getMessages().getRequirePlayer().send(sender);
            return;
        }

        Player focus = target != null ? target : (Player) sender;
        TextMessage message = target != null ? messages.getPingOther() : messages.getPingSelf();
        message.send(sender, "ping", focus.getPing(), "target", focus.getName());
    }

    /**
     * Check the average latency to the server
     *
     * @param sender The sender who is running the command
     */
    @Command("ping|eping|latency average")
    @Permission("essentials.ping.average")
    @CommandDescription("Check your current latency to the server")
    public void executeAverage(CommandSender sender) {
        BasicMessages messages = BasicMessages.get();
        int average = (int) Bukkit.getOnlinePlayers().stream().mapToInt(Player::getPing).average().orElse(-1.0);
        messages.getPingAverage().send(sender, "ping", average);
    }


}
