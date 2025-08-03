package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicConfig;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.argument.FlagArgumentHandler;
import dev.oribuin.essentials.util.CommandFlag;
import dev.oribuin.essentials.util.Cooldown;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class RepairCommand extends BaseRoseCommand {

    private final CommandFlag<String> ALL = new CommandFlag<>("all");
    private final Cooldown<UUID> cooldown = new Cooldown<>();

    public RepairCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, CommandFlag<String> all) {
        Player player = (Player) context.getSender();

        // Check if a player is on cooldown
        if (this.cooldown.onCooldown(player.getUniqueId())) {
            BasicMessages.REPAIR_COOLDOWN.send(player);
            return;
        }

        List<ItemStack> applicable = new ArrayList<>();
        // Add all the player items if available 
        if (all != null && player.hasPermission("essentials.fix.all")) {
            for (ItemStack stack : player.getInventory().getContents()) {
                if (stack == null || stack.getType().isAir()) continue;

                Integer damage = stack.getData(DataComponentTypes.DAMAGE);
                if (damage == null || damage <= 0) continue;

                applicable.add(stack);
            }
        } else { // Add only the item in the player's main hand
            ItemStack stack = player.getInventory().getItemInMainHand();
            Integer damage = stack.getData(DataComponentTypes.DAMAGE);
            if (damage != null && damage > 0) applicable.add(stack);
        }

        // Check if any of the tools are damaged
        if (applicable.isEmpty()) {
            BasicMessages.REPAIR_NO_DAMAGED.send(player);
            return;
        }

        // Repair the items and send the message
        this.cooldown.setCooldown(player.getUniqueId(), BasicConfig.REPAIR_COOLDOWN.value());
        applicable.forEach(x -> x.resetData(DataComponentTypes.DAMAGE));
        BasicMessages.REPAIR_COMMAND.send(player, "amount", applicable.size());
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("repair")
                .aliases("fix", "erepair", "efix")
                .permission("essentials.repair")
                .arguments(ArgumentsDefinition.builder().optional(
                        "all", new FlagArgumentHandler(ALL)
                ).build())
                .playerOnly(true)
                .build();
    }

}
