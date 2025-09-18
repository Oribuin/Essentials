package dev.oribuin.essentials.addon.chat.command;

import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.addon.chat.config.ChatMessages;
import dev.oribuin.essentials.addon.chat.database.ChatSender;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class RealNameCommand implements AddonCommand {

    private final ChatAddon addon;

    public RealNameCommand(ChatAddon addon) {
        this.addon = addon;
    }
    
    /**
     * Check the real name of a player by their nickname
     *
     * @param sender The sender who is running the command
     */
    @Command("realname <name>")
    @Permission("essentials.nickname.other")
    @CommandDescription("Check the real name of a player by their nickname")
    public void execute(Player sender, String name) {
        ChatMessages messages = ChatMessages.getInstance();
        ChatSender chatSender = this.addon.getRepository().get(sender.getUniqueId());
        
        // todo: make sure you cant rat out of a staff member with it
    }

}
