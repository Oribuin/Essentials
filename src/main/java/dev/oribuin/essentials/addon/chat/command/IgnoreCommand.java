package dev.oribuin.essentials.addon.chat.command;

import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.addon.chat.config.ChatMessages;
import dev.oribuin.essentials.addon.chat.database.ChatSender;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.database.model.User;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.ArrayList;
import java.util.List;

public class IgnoreCommand implements AddonCommand {

    private final ChatAddon addon;

    public IgnoreCommand(ChatAddon addon) {
        this.addon = addon;
    }

    /**
     * Toggle the ignored status of a player
     *
     * @param sender The sender who is running the command
     */
    @Command("ignore|block <target>")
    @Permission("essentials.ignore")
    @CommandDescription("Toggle the ignored status of a player")
    public void ignoreUser(Player sender, Player target) {
        ChatMessages messages = ChatMessages.getInstance();
        ChatSender chatSender = this.addon.getRepository().get(sender.getUniqueId());

        boolean result = chatSender.toggleIgnore(target.getUniqueId());
        if (result) {
            messages.getPlayerIgnored().send(sender, "target", target.getName());
        } else {
            messages.getPlayerUnignored().send(sender, "target", target.getName());
        }
    }

    /**
     * View the list of all the players that you have blocked
     *
     * @param sender The sender who is running the command
     */
    @Command("ignore|block list")
    @Permission("essentials.ignore")
    @CommandDescription("View the list of all the players that you have blocked")
    public void ignoreList(Player sender) {
        ChatMessages messages = ChatMessages.getInstance();
        ChatSender chatSender = this.addon.getRepository().get(sender.getUniqueId());
        String delimiter = messages.getIgnoreFormat().message();

        int total = chatSender.getIgnoredUsers().size();
        List<User> ignored = new ArrayList<>(List.of(new User(sender, System.currentTimeMillis()))); // todo: Require DataManager
        String ignoredUsers = String.join(delimiter != null ? delimiter : ", ", ignored
                .stream()
                .map(User::name)
                .toList()
        );

        messages.getIgnoreList().send(sender,
                "total", total,
                "ignored", ignoredUsers);
    }

}
