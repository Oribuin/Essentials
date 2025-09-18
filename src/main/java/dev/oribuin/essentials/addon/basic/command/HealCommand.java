package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicConfig;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.util.model.Cooldown;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class HealCommand implements AddonCommand {

    private final Cooldown<CommandSender> cooldown = new Cooldown<>();

    /**
     * Heal the player from their injuries
     *
     * @param sender The sender who is running the command
     */
    @Command("heal|eheal")
    @Permission("essentials.heal")
    @CommandDescription("Heal yourself from your injuries")
    public void execute(Player sender) {
        BasicMessages messages = BasicMessages.get();

        // Check if target is on cooldown
        if (this.cooldown.onCooldown(sender)) {
            long remaining = this.cooldown.getDurationRemaining(sender).getSeconds();
            messages.getHealCooldown().send(sender, "time", remaining);
            return;
        }

        // Apply cooldown to target
        this.cooldown.setCooldown(sender, BasicConfig.get().getHealCooldown());

        this.healTarget(sender);
        messages.getHealSelf().send(sender, "target", sender.getName());
    }

    /**
     * Heal the player from their injuries
     *
     * @param sender The sender who is running the command
     * @param target The target of the command
     */
    @Command("heal|eheal <target>")
    @Permission("essentials.heal.others")
    @CommandDescription("Heal yourself from your injuries")
    public void executeOther(CommandSender sender, Player target) {
        BasicMessages messages = BasicMessages.get();

        // Check if target is on cooldown
        if (this.cooldown.onCooldown(sender)) {
            long remaining = this.cooldown.getDurationRemaining(sender).getSeconds();
            messages.getHealCooldown().send(sender, "time", remaining);
            return;
        }

        // Apply cooldown to target
        this.cooldown.setCooldown(sender, BasicConfig.get().getHealCooldown());

        this.healTarget(target);
        messages.getHealOther().send(sender, "target", target.getName());
    }


    /**
     * Fully heal a player's health and saturation
     *
     * @param player The player to heal
     */
    private void healTarget(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute == null) return;

        player.setHealth(attribute.getValue());
        player.setFoodLevel(20);
        player.setSaturation(10);
    }
}
