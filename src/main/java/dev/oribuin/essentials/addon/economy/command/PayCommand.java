package dev.oribuin.essentials.addon.economy.command;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.oribuin.essentials.addon.economy.EconomyAddon;
import dev.oribuin.essentials.addon.economy.config.EconomyConfig;
import dev.oribuin.essentials.addon.economy.config.EconomyMessages;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
import dev.oribuin.essentials.addon.economy.model.Transaction;
import dev.oribuin.essentials.addon.economy.util.NumberUtil;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.util.StringPlaceholders;
import dev.oribuin.essentials.util.model.Placeholders;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PayCommand implements AddonCommand {

    private final Cache<UUID, Boolean> confirmation = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    /**
     * Add money to a users account
     *
     * @param sender The sender who is running the command
     * @param target The target of the command
     * @param amount The amount of money to change
     */
    @Command("economy|eco pay <target> <amount>")
    @Permission("essentials.economy.pay")
    @CommandDescription("Add money to a users account")
    public void execute(Player sender, Player target, Double amount) {
        EconomyMessages messages = EconomyMessages.get();
        EconomyRepository repository = EconomyAddon.getInstance().getRepository();

        // don't allow to add negative amounts (which is just withdrawing but use eco take for that weirdo)
        if (amount <= 0) {
            messages.getNoNegatives().send(sender, "amount", NumberUtil.format(amount));
            return;
        }

        // paying self just ends in loss of money 
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            messages.getPaySelf().send(sender);
            return;
        }

        if (EconomyConfig.get().isPayConfirm()) {
            Boolean result = this.confirmation.getIfPresent(sender.getUniqueId());

            if (result == null) {
                this.confirmation.put(sender.getUniqueId(), true);
                messages.getConfirmCommand().send(sender,
                        "amount", NumberUtil.format(amount),
                        "target", target.getName()
                );
                return;
            }
        }

        // make sure they have enough
        double balance = repository.getBalance(sender.getUniqueId()).getAmount().doubleValue();
        if (balance < amount) {
            messages.getNotEnough().send(sender, Placeholders.of(
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
            messages.getTransactionFailed().send(sender);
            return;
        }

        StringPlaceholders placeholders = Placeholders.of(
                "amount", NumberUtil.format(amount),
                "balance_player", NumberUtil.format(paymentSent.getCurrent()),
                "balance_target", NumberUtil.format(paymentReceived.getChange()),
                "target", target.getName(),
                "player", sender.getName()
        );

        messages.getPaidUser().send(sender, placeholders);
        messages.getPaidByUser().send(target, placeholders);
    }

}
