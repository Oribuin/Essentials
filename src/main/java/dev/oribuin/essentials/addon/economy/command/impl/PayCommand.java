package dev.oribuin.essentials.addon.economy.command.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.economy.config.EconomyConfig;
import dev.oribuin.essentials.addon.economy.config.EconomyMessages;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
import dev.oribuin.essentials.addon.economy.model.Transaction;
import dev.oribuin.essentials.addon.economy.util.NumberUtil;
import dev.oribuin.essentials.util.Placeholders;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PayCommand extends BaseRoseCommand {

    private final Cache<UUID, Boolean> confirmation = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    public PayCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target, Double amount) {
        Player sender = (Player) context.getSender();
        EconomyRepository repository = AddonProvider.ECONOMY_ADDON.repository();

        // don't allow to add negative amounts (which is just withdrawing but use eco take for that weirdo)
        if (amount <= 0) {
            EconomyMessages.NO_NEGATIVES.send(sender, Placeholders.of("amount", NumberUtil.format(amount)));
            return;
        }

        // paying self just ends in loss of money 
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            EconomyMessages.PAY_SELF.send(sender);
            return;
        }

        if (EconomyConfig.PAY_CONFIRM.value()) {
            Boolean result = this.confirmation.getIfPresent(sender.getUniqueId());

            if (result == null) {
                this.confirmation.put(sender.getUniqueId(), true);
                EconomyMessages.CONFIRM_COMMAND.send(sender, Placeholders.of(
                        "amount", NumberUtil.format(amount),
                        "player", target.getName()
                ));
                return;
            }
        }

        // make sure they have enough
        double balance = repository.getBalance(sender.getUniqueId()).amount().doubleValue();
        if (balance < amount) {
            EconomyMessages.NOT_ENOUGH.send(sender, Placeholders.of(
                    "amount", NumberUtil.format(amount),
                    "balance", NumberUtil.format(balance),
                    "required", NumberUtil.format(amount - balance)
            ));
            return;
        }

        this.confirmation.invalidate(sender.getUniqueId());

        // Give the money to the target
        String source = "user %s[%s] paid target %s[%s] amount[%s] via[%s]";
        Object[] sourcePlaceholders = new Object[]{ sender.getName(), sender.getUniqueId(), target.getName(), target.getUniqueId(), amount, "Command" };
        Transaction paymentSent = repository.offset(sender.getUniqueId(),
                BigDecimal.valueOf(-amount),
                String.format(source, sourcePlaceholders)
        );

        Transaction paymentReceived = repository.offset(
                target.getUniqueId(),
                BigDecimal.valueOf(amount),
                String.format(source, sourcePlaceholders)
        );


        // check if transaction has failed
        if (paymentSent == null || paymentReceived == null) {
            EconomyMessages.TRANSACTION_FAILED.send(sender);
            return;
        }

        StringPlaceholders placeholders = Placeholders.of(
                "amount", NumberUtil.format(amount),
                "balance_player", NumberUtil.format(paymentSent.current()),
                "balance_target", NumberUtil.format(paymentReceived.change()),
                "target", target.getName(),
                "player", sender.getName()
        );

        EconomyMessages.PAID_USER.send(sender, placeholders);
        EconomyMessages.PAID_BY_USER.send(target, placeholders);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("pay")
                .permission("essentials.economy.pay")
                .playerOnly(true)
                .arguments(this.createArgumentsDefinition())
                .build();
    }

    private ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("target", ArgumentHandlers.PLAYER)
                .required("amount", ArgumentHandlers.DOUBLE)
                .build();
    }
}
