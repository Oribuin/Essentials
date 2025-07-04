package dev.oribuin.essentials.addon.economy.command;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.economy.command.impl.AddBalanceCommand;
import dev.oribuin.essentials.addon.economy.command.impl.SetBalanceCommand;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
import dev.oribuin.essentials.addon.economy.model.UserAccount;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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
        CommandSender sender = context.getSender();
        UUID user = target != null ? target.getUniqueId() : ((Player) context.getSender()).getUniqueId();
        sender.sendMessage("Loading User Balance");


        UserAccount balance = repository.getBalance(user);
         sender.sendMessage(String.format(
                "User[%s] balance[%s] time_since_update[%ss]",
                user, balance.amount(), (System.currentTimeMillis() - balance.lastUpdated()) / 1000
        ));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("economy")
                .aliases("eco", "balance", "bal")
                .permission("essentials.economy.use")
                .arguments(
                        ArgumentsDefinition.builder()
                                .optional("target", ArgumentHandlers.OFFLINE_PLAYER)
                                .optionalSub(new SetBalanceCommand(this.rosePlugin), new AddBalanceCommand(this.rosePlugin))
                )
                //                .arguments(this.sub(new SetBalanceCommand(this.rosePlugin)))
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
