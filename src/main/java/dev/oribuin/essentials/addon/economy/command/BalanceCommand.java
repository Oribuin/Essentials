package dev.oribuin.essentials.addon.economy.command;

import dev.oribuin.essentials.addon.economy.EconomyAddon;
import dev.oribuin.essentials.addon.economy.config.EconomyMessages;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
import dev.oribuin.essentials.addon.economy.model.UserAccount;
import dev.oribuin.essentials.addon.economy.util.NumberUtil;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class BalanceCommand implements AddonCommand {

    /**
     * Check a player's current balance
     *
     * @param sender The sender who is running the command
     */
    @Command("economy|eco|bal|balance")
    @Permission("essentials.economy.balance")
    @CommandDescription("Check a player's current balance")
    public void execute(Player sender) {
        EconomyMessages messages = EconomyMessages.get();
        EconomyRepository repository = EconomyAddon.getInstance().getRepository();

        UserAccount account = repository.getBalance(sender.getUniqueId());
        messages.getCurrentBalance().send(sender, "balance", NumberUtil.format(account.getAmount()));
    }

    /**
     * Check a player's current balance
     *
     * @param sender The sender who is running the command
     */
    @Command("economy|eco|bal|balance <target>")
    @Permission("essentials.economy.balance.others")
    @CommandDescription("Check a player's current balance")
    public void executeOther(CommandSender sender, Player target) {
        EconomyMessages messages = EconomyMessages.get();
        EconomyRepository repository = EconomyAddon.getInstance().getRepository();

        UserAccount account = repository.getBalance(target.getUniqueId());
        messages.getTargetBalance().send(sender, "target", target.getName(), "balance", NumberUtil.format(account.getAmount()));
    }

}
