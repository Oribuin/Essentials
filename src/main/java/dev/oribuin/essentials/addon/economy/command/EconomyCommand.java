package dev.oribuin.essentials.addon.economy.command;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.economy.command.impl.AddBalanceCommand;
import dev.oribuin.essentials.addon.economy.command.impl.PayCommand;
import dev.oribuin.essentials.addon.economy.command.impl.SetBalanceCommand;
import dev.oribuin.essentials.addon.economy.command.impl.TakeBalanceCommand;
import dev.oribuin.essentials.addon.economy.config.EconomyMessages;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
import dev.oribuin.essentials.addon.economy.model.UserAccount;
import dev.oribuin.essentials.addon.economy.util.NumberUtil;
import dev.oribuin.essentials.command.argument.UserArgumentHandler;
import dev.oribuin.essentials.util.Placeholders;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class EconomyCommand extends BaseRoseCommand {

    public EconomyCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, OfflinePlayer target) {
        if (target == null && !(context.getSender() instanceof Player)) {
            context.getSender().sendMessage("You need to be or target a player to run this command");
            return;
        }

        EconomyRepository repository = AddonProvider.ECONOMY_ADDON.repository();

        // send the targets balance
        if (target != null && context.getSender().hasPermission("essentials.economy.use.other")) {
            UserAccount account = repository.getBalance(target.getUniqueId());
            EconomyMessages.TARGET_BALANCE.send(context.getSender(), "target", target.getName(), "balance", NumberUtil.format(account.amount()));
            return;
        }

        // send the senders balance 
        if (context.getSender() instanceof Player player) {
            UserAccount account = repository.getBalance(player.getUniqueId());
            StringPlaceholders placeholders = Placeholders.of("balance", NumberUtil.format(account.amount()));
            EconomyMessages.CURRENT_BALANCE.send(player, placeholders);
        }
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("economy")
                .aliases("eco", "balance", "bal")
                .permission("essentials.economy.use")
                .arguments(
                        ArgumentsDefinition.builder()
                                .optional("target", new UserArgumentHandler())
                                .optionalSub(
                                        new AddBalanceCommand(this.rosePlugin),
                                        new PayCommand(this.rosePlugin),
                                        new SetBalanceCommand(this.rosePlugin),
                                        new TakeBalanceCommand(this.rosePlugin)
                                )
                )
                .build();
    }

    /**
     * Define all the subcommands for this base command
     *
     * @param subs The subcommands to add
     *
     * @return The argument definition for the command
     */
    private ArgumentsDefinition sub(BaseRoseCommand... subs) {
        return ArgumentsDefinition.builder().optionalSub(subs);
    }

}
