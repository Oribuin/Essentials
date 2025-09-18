package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicConfig;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.util.model.Cooldown;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class RepairCommand implements AddonCommand {

    private final Cooldown<UUID> cooldown = new Cooldown<>();

    /**
     * Fix the item that you're holding
     *
     * @param sender The sender who is running the command
     */
    @Command("repair|erepair|fix|efix")
    @Permission("essentials.fix")
    @CommandDescription("Fix the item that you're holding")
    public void fixHand(Player sender) {
        BasicConfig config = BasicConfig.get();
        BasicMessages messages = BasicMessages.get();

        // Check if a player is on cooldown
        if (this.cooldown.onCooldown(sender.getUniqueId())) {
            long remaining = this.cooldown.getDurationRemaining(sender.getUniqueId()).getSeconds();
            messages.getRepairCooldown().send(sender, "time", remaining);
            return;
        }

        // Check if the item being held is damaged
        ItemStack stack = sender.getInventory().getItemInMainHand();
        Integer damage = stack.getData(DataComponentTypes.DAMAGE);
        if (damage == null || damage <= 0) {
            messages.getRepairNoDamage().send(sender);
            return;
        }

        // Repair the items and send the message
        this.cooldown.setCooldown(sender.getUniqueId(), config.getRepairCooldown());
        stack.resetData(DataComponentTypes.DAMAGE);
        messages.getRepairCommand().send(sender, "amount", 1);
    }

    /**
     * Fix the item that you're holding
     *
     * @param sender The sender who is running the command
     */
    @Command("repair|erepair|fix|efix all")
    @Permission("essentials.fix.all")
    @CommandDescription("Fix the item that you're holding")
    public void fixAll(Player sender) {
        BasicConfig config = BasicConfig.get();
        BasicMessages messages = BasicMessages.get();

        // Check if a player is on cooldown
        if (this.cooldown.onCooldown(sender.getUniqueId())) {
            long remaining = this.cooldown.getDurationRemaining(sender.getUniqueId()).getSeconds();
            messages.getRepairCooldown().send(sender, "time", remaining);
            return;
        }

        List<ItemStack> applicable = new ArrayList<>();
        // Add all the player items if available 
        for (ItemStack stack : sender.getInventory().getContents()) {
            if (stack == null || stack.getType().isAir()) continue;

            Integer damage = stack.getData(DataComponentTypes.DAMAGE);
            if (damage == null || damage <= 0) continue;

            applicable.add(stack);
        }

        // Check if any items need to be repaired
        if (applicable.isEmpty()) {
            messages.getRepairNoDamage().send(sender);
            return;
        }

        // Repair the items and send the message
        this.cooldown.setCooldown(sender.getUniqueId(), config.getRepairCooldown());
        applicable.forEach(x -> x.resetData(DataComponentTypes.DAMAGE));
        messages.getRepairCommand().send(sender, "amount", applicable.size());
    }

}
