package dev.oribuin.essentials.addon.economy.command;

import dev.oribuin.essentials.addon.economy.EconomyAddon;
import dev.oribuin.essentials.addon.economy.config.EconomyMessages;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
import dev.oribuin.essentials.addon.economy.util.NumberUtil;
import dev.oribuin.essentials.util.model.Placeholders;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.math.BigDecimal;

public class SetBalanceCommand {

    /**
     * Add money to a users account
     *
     * @param sender The sender who is running the command
     * @param target The target of the command
     * @param amount The amount of money to change
     */
    @Command("economy|eco set <target> <amount>")
    @Permission("essentials.economy.set")
    @CommandDescription("Add money to a users account")
    public void execute(CommandSender sender, Player target, Double amount) {
        EconomyMessages messages = EconomyMessages.get();
        EconomyRepository repository = EconomyAddon.getInstance().getRepository();

        // don't allow to set negative amounts
        if (amount <= 0) {
            messages.getNoNegatives().send(sender, Placeholders.of("amount", NumberUtil.format(amount)));
            return;
        }

        String source = "user[%s] set target %s[%s] amount[%s] via[%s]";

        boolean result = repository.set(
                target.getUniqueId(),
                BigDecimal.valueOf(amount),
                String.format(source,
                        sender.getName(),
                        target.getName(),
                        target.getUniqueId(),
                        NumberUtil.format(amount),
                        "Command"
                )
        );
        // check if transaction has failed
        if (!result) {
            messages.getTransactionFailed().send(sender);
            return;
        }

        messages.getSetBalance().send(sender,
                "amount", NumberUtil.format(amount),
                "balance", NumberUtil.format(amount),
                "target", target.getName()
        );
    }

}
