package dev.oribuin.essentials.addon.economy.command.impl;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.economy.config.EconomyMessages;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
import dev.oribuin.essentials.addon.economy.model.Transaction;
import dev.oribuin.essentials.addon.economy.util.NumberUtil;
import dev.oribuin.essentials.command.argument.UserArgumentHandler;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;

public class TakeBalanceCommand extends BaseRoseCommand {

    public TakeBalanceCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, OfflinePlayer target, Double amount) {
        CommandSender sender = context.getSender();
        EconomyRepository repository = AddonProvider.ECONOMY_ADDON.repository();

        // don't allow to take negative amounts (which is just withdrawing but use eco add for that weirdo)
        if (amount <= 0) {
            EconomyMessages.NO_NEGATIVES.send(sender, StringPlaceholders.of("amount", NumberUtil.format(amount)));
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
            EconomyMessages.TRANSACTION_FAILED.send(sender);
            return;
        }

        EconomyMessages.REMOVED_BALANCE.send(sender, StringPlaceholders.of(
                "amount", NumberUtil.format(amount),
                "balance", NumberUtil.format(transaction.current())
        ));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("take")
                .permission("essentials.economy.take")
                .playerOnly(false)
                .arguments(this.createArgumentsDefinition())
                .build();
    }

    private ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("target", new UserArgumentHandler())
                .required("amount", ArgumentHandlers.DOUBLE)
                .build();
    }
}
