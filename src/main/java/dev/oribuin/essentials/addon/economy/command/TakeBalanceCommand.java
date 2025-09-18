package dev.oribuin.essentials.addon.economy.command;

import dev.oribuin.essentials.addon.economy.EconomyAddon;
import dev.oribuin.essentials.addon.economy.config.EconomyMessages;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
import dev.oribuin.essentials.addon.economy.model.Transaction;
import dev.oribuin.essentials.addon.economy.util.NumberUtil;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.util.model.Placeholders;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.math.BigDecimal;

public class TakeBalanceCommand implements AddonCommand {

    /**
     * Add money to a users account
     *
     * @param sender The sender who is running the command
     * @param target The target of the command
     * @param amount The amount of money to change
     */
    @Command("economy|eco remove|take <target> <amount>")
    @Permission("essentials.economy.take")
    @CommandDescription("Add money to a users account")
    public void execute(CommandSender sender, OfflinePlayer target, Double amount) {
        EconomyMessages messages = EconomyMessages.get();
        EconomyRepository repository = EconomyAddon.getInstance().getRepository();

        // don't allow to take negative amounts (which is just withdrawing but use eco add for that weirdo)
        if (amount <= 0) {
            messages.getNoNegatives().send(sender, Placeholders.of("amount", NumberUtil.format(amount)));
            return;
        }

        // Give the money to the target
        String source = "user[%s] removing from target %s[%s] amount[%s] via[%s]";
        Transaction transaction = repository.offset(target.getUniqueId(), BigDecimal.valueOf(-amount), String.format(source,
                sender.getName(),
                target.getName(),
                target.getUniqueId(),
                NumberUtil.format(amount),
                "Command"
        ));

        // check if transaction has failed
        if (transaction == null) {
            messages.getTransactionFailed().send(sender);
            return;
        }

        messages.getRemovedBalance().send(sender,
                "amount", NumberUtil.format(amount),
                "balance", NumberUtil.format(transaction.getCurrent()),
                "target", target.getName()
        );
    }

}
