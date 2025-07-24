package dev.oribuin.essentials.addon.economy.command.impl;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.economy.config.EconomyMessages;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
import dev.oribuin.essentials.addon.economy.util.NumberUtil;
import dev.oribuin.essentials.command.argument.UserArgumentHandler;
import dev.oribuin.essentials.util.Placeholders;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class SetBalanceCommand extends BaseRoseCommand {

    public SetBalanceCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target, Double amount) {
        CommandSender sender = context.getSender();
        // don't allow to set negative amounts
        if (amount <= 0) {
            EconomyMessages.NO_NEGATIVES.send(sender, Placeholders.of("amount", NumberUtil.format(amount)));
            return;
        }

        EconomyRepository repository = AddonProvider.ECONOMY_ADDON.repository();
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
            EconomyMessages.TRANSACTION_FAILED.send(sender);
            return;
        }

        EconomyMessages.SET_BALANCE.send(sender, Placeholders.of(
                "amount", NumberUtil.format(amount),
                "balance", NumberUtil.format(amount)
        ));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("set")
                .permission("essentials.economy.set")
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
