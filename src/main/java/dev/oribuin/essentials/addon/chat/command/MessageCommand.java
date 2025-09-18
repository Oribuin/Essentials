package dev.oribuin.essentials.addon.chat.command;

import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.addon.chat.config.ChatMessages;
import dev.oribuin.essentials.addon.chat.database.ChatRepository;
import dev.oribuin.essentials.addon.chat.database.ChatSender;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.util.StringPlaceholders;
import dev.oribuin.essentials.util.model.Placeholders;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class MessageCommand implements AddonCommand {

    private final ChatAddon addon;

    public MessageCommand(ChatAddon addon) {
        this.addon = addon;
    }

    /**
     * Send the initial message to a player
     *
     * @param sender  The player to send the message to
     * @param target  The target of the message
     * @param message The message being sent
     */
    @Command("message|msg|m|whisper|w|tell <target> <message>")
    @Permission("essentials.msg")
    @CommandDescription("Send a message to a player")
    public void message(Player sender, Player target, @Greedy String message) {
        this.handle(sender, target, message);
    }

    /**
     * Reply to a message from a player
     *
     * @param sender  The player to send the message to
     * @param message The message being sent
     */
    @Command("reply|r <message>")
    @Permission("essentials.reply")
    @CommandDescription("Reply to a message from a player")
    public void reply(Player sender, @Greedy String message) {
        ChatSender chatSender = this.addon.getRepository().get(sender.getUniqueId());
        ChatSender lastMessaged = chatSender.getLastMessaged();
        ChatMessages messages = ChatMessages.getInstance();

        // does the chat sender even exist in the first place
        if (lastMessaged == null) {
            messages.getNobodyToReply().send(sender);
            return;
        }
        
        // did the user log off
        Player target = lastMessaged.getPlayer();
        if (target == null) {
            messages.getNobodyToReply().send(sender);
            return;
        }
        
        // send the reply message
        this.handle(sender, target, message);
    }

    /**
     * Handle all the direct messages through one method
     *
     * @param sender   The person sending the message
     * @param receiver The person receiving the message
     * @param message  The message being sent
     */
    private void handle(Player sender, Player receiver, String message) {
        ChatRepository repository = this.addon.getRepository();
        ChatSender user = repository.get(sender.getUniqueId());
        ChatSender recipient = repository.get(receiver.getUniqueId());
        ChatMessages messages = ChatMessages.getInstance();

        if (!sender.canSee(sender)) {
            sender.sendMessage("who"); // todo
            return;
        }

        // User can't message someone they've ignored
        if (user.hasIgnored(recipient.getUuid())) {
            messages.getCannotDmIgnored().send(sender);
            return;
        }

        // Recipient has their messages closed
        if (!recipient.isOpenDms()) {
            messages.getUserMessagesClosed().send(sender);
            return;
        }

        StringPlaceholders placeholders = Placeholders.of(
                "message", message,
                "sender", sender.getName(),
                "recipient", receiver.getName()
        );

        // If the recipient doesn't have the user ignored, send the message
        // we don't tell the sender they've gotten ignored because that feels like rank #1 way to get harassment reports
        if (!recipient.hasIgnored(user.getUuid())) {
            user.setLastMessaged(recipient);
            messages.getDirectMessageReceived().send(receiver, placeholders);
        }

        messages.getDirectMessageSent().send(sender, placeholders);
        messages.getSocialSpyMessage().send(
                this.addon.getSocialSpyAudience(sender.getUniqueId(), receiver.getUniqueId()),
                placeholders
        );
    }

}
