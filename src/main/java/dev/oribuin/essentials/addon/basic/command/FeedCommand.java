package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicConfig;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.util.model.Cooldown;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class FeedCommand implements AddonCommand {

    private final Cooldown<CommandSender> cooldown = new Cooldown<>();

    /**
     * Feed a player and heal their saturation levels
     *
     * @param sender The sender who is running the command
     */
    @Command("feed|efeed")
    @Permission("essentials.feed")
    @CommandDescription("Fill up your own hunger bar")
    public void execute(Player sender) {
        BasicMessages messages = BasicMessages.get();

        // Check if target is on cooldown
        if (this.cooldown.onCooldown(sender)) {
            long remaining = this.cooldown.getDurationRemaining(sender).getSeconds();
            messages.getFeedCooldown().send(sender, "time", remaining);
            return;
        }

        // Apply cooldown to target
        this.cooldown.setCooldown(sender, BasicConfig.get().getFeedCooldown());

        // Feed the target
        sender.setFoodLevel(20);
        sender.setSaturation(10);
        messages.getFeedSelf().send(sender);
    }

    /**
     * Feed a player and heal their saturation levels
     *
     * @param sender The sender who is running the command
     * @param target The target of the command
     */
    @Command("feed|efeed <target>")
    @Permission("essentials.feed.others")
    @CommandDescription("Fill up another player's hunger bar")
    public void executeOther(CommandSender sender, Player target) {
        BasicMessages messages = BasicMessages.get();

        // Check if target is on cooldown
        if (this.cooldown.onCooldown(sender)) {
            messages.getFeedCooldown().send(sender);
            return;
        }

        // Apply cooldown to target
        this.cooldown.setCooldown(sender, BasicConfig.get().getFeedCooldown());

        // Feed the target
        target.setFoodLevel(20);
        target.setSaturation(10);
        messages.getFeedOther().send(sender, "target", target.getName());
    }

}
