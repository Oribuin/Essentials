package dev.oribuin.essentials.addon.economy.config;

import dev.oribuin.essentials.addon.economy.EconomyAddon;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.config.TextMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class EconomyMessages implements AddonConfig {

    private static final String PREFIX = "<#bc7dff><b>Economy</b> <gray>| <white>";

    public static EconomyMessages get() {
        return EconomyAddon.getInstance().getConfigLoader().get(EconomyMessages.class);
    }

    private TextMessage currentBalance = new TextMessage(PREFIX + "You currently have [<#bc7dff><balance><white>] in your balance");
    private TextMessage targetBalance = new TextMessage(PREFIX + "<#bc7dff><target> <white>currently has [<#bc7dff><balance><white>] in their balance");
    private TextMessage addedBalance = new TextMessage(PREFIX + "You have given <#bc7dff>+<amount> <white>money. Total [<#bc7dff><balance><white>]");
    private TextMessage removedBalance = new TextMessage(PREFIX + "You have taken <#bc7dff>-<amount> <white>money from <target>. Total [<#bc7dff><balance><white>]");
    private TextMessage setBalance = new TextMessage(PREFIX + "You have set <#bc7dff><target><white>'s balance to [<#bc7dff><amount><white>]");
    private TextMessage transactionFailed = new TextMessage(PREFIX + "The transaction unfortunately failed, please try again.");
    private TextMessage noNegatives = new TextMessage(PREFIX + "You cannot add/take negative amounts.");
    private TextMessage paySelf = new TextMessage(PREFIX + "You cannot pay yourself");
    @Comment("The message sent when a player needs to confirm their payment")
    private TextMessage confirmCommand = new TextMessage(PREFIX + "Please type the command again to confirm you want to pay <#bc7dff><target> <white>[<#bc7dff><amount><white>].");
    private TextMessage paidUser = new TextMessage(PREFIX + "You have paid <#bc7dff><target> <white>[<#bc7dff><amount><white>]");
    private TextMessage paidByUser = new TextMessage(PREFIX + "You have been paid by <#bc7dff><target><white> a total of [<#bc7dff><amount><white>]");
    private TextMessage notEnough = new TextMessage(PREFIX + "You do not have enough money to pay this user");

    public TextMessage getCurrentBalance() {
        return currentBalance;
    }

    public TextMessage getTargetBalance() {
        return targetBalance;
    }

    public TextMessage getAddedBalance() {
        return addedBalance;
    }

    public TextMessage getRemovedBalance() {
        return removedBalance;
    }

    public TextMessage getSetBalance() {
        return setBalance;
    }

    public TextMessage getTransactionFailed() {
        return transactionFailed;
    }

    public TextMessage getNoNegatives() {
        return noNegatives;
    }

    public TextMessage getPaySelf() {
        return paySelf;
    }

    public TextMessage getConfirmCommand() {
        return confirmCommand;
    }

    public TextMessage getPaidUser() {
        return paidUser;
    }

    public TextMessage getPaidByUser() {
        return paidByUser;
    }

    public TextMessage getNotEnough() {
        return notEnough;
    }
}
