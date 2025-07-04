package dev.oribuin.essentials.addon.economy.command.impl;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
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

public class AddBalanceCommand extends BaseRoseCommand {

    public AddBalanceCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target, Double amount) {
        CommandSender sender = context.getSender();
        sender.sendMessage("Adding User Balance");

        EconomyRepository repository = AddonProvider.ECONOMY_ADDON.repository();
        String source = "user[%s] adding to target %s[%s] amount[%s] via[%s]";

        repository.offset(target.getUniqueId(), BigDecimal.valueOf(amount), String.format(source,
                sender.getName(),
                target.getName(),
                target.getUniqueId(),
                amount,
                "Command"
        ));

        System.out.println("Added money (or removed) to (or from) balance :)");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("add")
                .permission("essentials.economy.add")
                .playerOnly(false)
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
