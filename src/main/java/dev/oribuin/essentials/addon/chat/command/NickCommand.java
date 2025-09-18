package dev.oribuin.essentials.addon.chat.command;

import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.addon.chat.config.ChatMessages;
import dev.oribuin.essentials.addon.chat.database.ChatRepository;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
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
    public void execute(Player sender, @Greedy String nick) {
        ChatMessages messages = ChatMessages.getInstance();
        ChatRepository repository = this.addon.getRepository();

        repository.getAsync(sender.getUniqueId()).thenAcceptAsync(chatSender -> {
            chatSender.setNickname(nick);
            repository.saveUser(chatSender);

            if (nick != null) {
                messages.getSetNickname().send(sender, "nickname", nick);
                return;
            }

            messages.getResetNickname().send(sender);
        });
    }

    /**
     * Change your display nickname on the server
     *
     * @param sender The sender who is running the command
     */
    @Command("modifynickname|modifynick <target> [nick]")
    @Permission("essentials.nickname.other")
    @CommandDescription("Change your display nickname on the server")
    public void execute(CommandSender sender, Player target, @Greedy String nick) {
        ChatMessages messages = ChatMessages.getInstance();
        ChatRepository repository = this.addon.getRepository();

        repository.getAsync(target.getUniqueId()).thenAcceptAsync(chatSender -> {
            chatSender.setNickname(nick);
            repository.saveUser(chatSender);

            if (nick != null) {
                messages.getSetNicknameOther().send(sender, "nickname", nick, "target", target.getName());
                return;
            }

            messages.getResetNicknameOther().send(sender, "target", target.getName());
        });
    }

}
