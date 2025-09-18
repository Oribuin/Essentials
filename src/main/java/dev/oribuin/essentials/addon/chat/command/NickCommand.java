package dev.oribuin.essentials.addon.chat.command;

import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.addon.chat.config.ChatMessages;
import dev.oribuin.essentials.addon.chat.database.ChatSender;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class NickCommand implements AddonCommand {

    private final ChatAddon addon;

    public NickCommand(ChatAddon addon) {
        this.addon = addon;
    }

    /**
     * Change your display nickname on the server
     *
     * @param sender The sender who is running the command
     */
    @Command("nickname|nick [nick]")
    @Permission("essentials.nickname")
    @CommandDescription("Change your display nickname on the server")
    public void execute(Player sender, String nick) {
        ChatMessages messages = ChatMessages.getInstance();
        ChatSender chatSender = this.addon.getRepository().get(sender.getUniqueId());
    }

    /**
     * Change your display nickname on the server
     *
     * @param sender The sender who is running the command
     */
    @Command("nickname|nick admin <target> [nick]")
    @Permission("essentials.nickname.other")
    @CommandDescription("Change your display nickname on the server")
    public void execute(Player sender, Player target, String nick) {
        ChatMessages messages = ChatMessages.getInstance();
        ChatSender chatSender = this.addon.getRepository().get(sender.getUniqueId());
    }

}
