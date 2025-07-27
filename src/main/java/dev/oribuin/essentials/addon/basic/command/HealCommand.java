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
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand extends BaseRoseCommand {

    private final Cooldown<CommandSender> cooldown = new Cooldown<>();

    public HealCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target) {
        // Swap the target if the sender does not have permission to heal another player 
        if (target != null && !context.getSender().hasPermission("essentials.health.others") && context.getSender() instanceof Player sender) {
            target = sender;
        }

        // Check if target is on cooldown
        if (this.cooldown.onCooldown(context.getSender())) {
            BasicMessages.HEAL_COOLDOWN.send(context.getSender());
            return;
        }

        // Apply cooldown to target
        this.cooldown.setCooldown(context.getSender(), BasicConfig.HEAL_COOLDOWN.value());

        // Send the ping message
        if (target != null) {
            this.healTarget(target);
            BasicMessages.HEAL_OTHER.send(context.getSender(), "target", target.getName());
            return;
        }

        Player player = (Player) context.getSender();

        // Heal the command sender
        BasicMessages.HEAL_COMMAND.send(player);
        this.healTarget(player);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("heal")
                .aliases("eheal")
                .permission("essentials.heal")
                .arguments(EssUtils.createTarget(true))
                .build();
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
        player.setSaturation(20);
    }
}
