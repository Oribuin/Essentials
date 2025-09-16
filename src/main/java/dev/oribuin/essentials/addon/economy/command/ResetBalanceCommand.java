package dev.oribuin.essentials.addon.economy.command;

import dev.oribuin.essentials.addon.economy.EconomyAddon;
import dev.oribuin.essentials.addon.economy.config.EconomyConfig;
import dev.oribuin.essentials.addon.economy.config.EconomyMessages;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
import dev.oribuin.essentials.addon.economy.util.NumberUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.math.BigDecimal;

public class ResetBalanceCommand {

    /**
     * Reset a user's current balance
     *
     * @param sender The sender who is running the command
     * @param target The target of the command
     */
    @Command("economy|eco reset <target>")
    @Permission("essentials.economy.reset")
    @CommandDescription("Add money to a users account")
    public void execute(CommandSender sender, Player target) {
        EconomyMessages messages = EconomyMessages.get();
        EconomyRepository repository = EconomyAddon.getInstance().getRepository();
        double starting = EconomyConfig.get().getStartingBalance();
        String source = "user[%s] set target %s[%s] amount[%s] via[%s]";

        boolean result = repository.set(
                target.getUniqueId(),
                BigDecimal.valueOf(starting),
                String.format(source,
                        sender.getName(),
                        target.getName(),
                        target.getUniqueId(),
                        NumberUtil.format(starting),
                        "Command"
                )
        );
        // check if transaction has failed
        if (!result) {
            messages.getTransactionFailed().send(sender);
            return;
        }

        messages.getSetBalance().send(sender,
                "amount", NumberUtil.format(starting),
                "balance", NumberUtil.format(starting),
                "target", target.getName()
        );
    }

}
