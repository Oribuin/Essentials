package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicConfig;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.util.Cooldown;
import dev.oribuin.essentials.util.EssUtils;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FeedCommand extends BaseRoseCommand {

    private final Cooldown<CommandSender> cooldown = new Cooldown<>();

    public FeedCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target) {
        // Swap the target if the sender does not have permission to feed another player 
        if (target != null && !context.getSender().hasPermission("essentials.feed.others") && context.getSender() instanceof Player sender) {
            target = sender;
        }

        // Check if target is on cooldown
        if (this.cooldown.onCooldown(context.getSender())) {
            BasicMessages.FEED_COOLDOWN.send(context.getSender());
            return;
        }

        // Apply cooldown to target
        this.cooldown.setCooldown(context.getSender(), BasicConfig.FEED_COOLDOWN.value());
        
        // Feed the target
        if (target != null) {
            target.setSaturation(20);
            target.setFoodLevel(20);
            BasicMessages.FEED_OTHER.send(context.getSender(), "target", target.getName());
            return;
        }

        Player player = (Player) context.getSender();
        
        // Feed the sender
        BasicMessages.FEED_COMMAND.send(player);
        player.setSaturation(20);
        player.setFoodLevel(20);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("feed")
                .aliases("efeed")
                .permission("essentials.feed")
                .arguments(EssUtils.createTarget(true))
                .build();
    }

}
