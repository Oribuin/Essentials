package dev.oribuin.essentials.addon.economy.config;

import dev.oribuin.essentials.api.config.option.Message;
import dev.oribuin.essentials.api.config.option.TextMessage;
import dev.oribuin.essentials.api.config.type.MessageConfig;

import java.util.List;

public class EconomyMessages extends MessageConfig {

    private static final String PREFIX = "<#bc7dff><b>Economy</b> <gray>| <white>";

    public static Message CURRENT_BALANCE = TextMessage.ofConfig(PREFIX + "You currently have [<#bc7dff>$<balance><white>]");
    public static Message TARGET_BALANCE = TextMessage.ofConfig(PREFIX + "<player> currently has [<#bc7dff>$<balance><white>]");
    public static Message ADDED_BALANCE = TextMessage.ofConfig(PREFIX + "You have given <#bc7dff>+$<amount> <white>money. Total [<#bc7dff><balance><white>]");
    public static Message REMOVED_BALANCE = TextMessage.ofConfig(PREFIX + "You have taken <#bc7dff>-$<amount> <white>money from <player>. Total [<#bc7dff><balance><white>]");
    public static Message SET_BALANCE = TextMessage.ofConfig(PREFIX + "You have set <#bc7dff><player><white>'s balance to [<#bc7dff>$<amount><white>]");
    public static Message TRANSACTION_FAILED = TextMessage.ofConfig(PREFIX + "The transaction unfortunately failed, please try again.");
    public static Message NO_NEGATIVES = TextMessage.ofConfig(PREFIX + "You cannot add/take negative amounts.");
    public static Message PAY_SELF = TextMessage.ofConfig(PREFIX + "You cannot pay yourself");
    public static Message CONFIRM_COMMAND = TextMessage.ofConfig(PREFIX + "Please type the command again to confirm you want to pay <#bc7dff><player> <white>[<#bc7dff>$<amount><white>].", List.of("The message sent when a player needs to confirm their payment"));
    public static Message PAID_USER = TextMessage.ofConfig(PREFIX + "You have paid <#bc7dff><target> <white>[<#bc7dff>$<amount><white>]");
    public static Message PAID_BY_USER = TextMessage.ofConfig(PREFIX + "You have been paid by <#bc7dff><player><white> a total of [<#bc7dff>$<amount><white>]");
    public static Message NOT_ENOUGH = TextMessage.ofConfig(PREFIX + "You do not have enough money to pay this user");

}
