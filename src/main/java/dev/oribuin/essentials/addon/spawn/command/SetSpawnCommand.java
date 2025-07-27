package dev.oribuin.essentials.addon.spawn.command;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.spawn.config.SpawnConfig;
import dev.oribuin.essentials.addon.spawn.config.SpawnMessages;
import dev.oribuin.essentials.api.config.option.Option;
import dev.oribuin.essentials.util.EssUtils;
import dev.oribuin.essentials.util.FinePosition;
import dev.oribuin.essentials.util.NumberUtil;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class SetSpawnCommand extends BaseRoseCommand {

    public SetSpawnCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, String center) {
        Player sender = (Player) context.getSender();

        Location location = sender.getLocation().clone();
        if (center != null) {
            location = location.toCenterLocation();
        }

        SpawnConfig config = AddonProvider.SPAWN_ADDON.config();
        Option<FinePosition> spawnpoint = SpawnConfig.SPAWNPOINT;
        spawnpoint.value(FinePosition.from(location));
        config.update(spawnpoint);
        config.save();
        
        location.getWorld().setSpawnLocation(location);
        SpawnMessages.SET_SPAWN.send(sender, 
                "x", NumberUtil.rounded(location.getX(), 3), 
                "y", NumberUtil.rounded(location.getY(), 3), 
                "z", NumberUtil.rounded(location.getZ(), 3)
        );
    }

    /**
     * @return a newly constructed {@link CommandInfo} for this command
     */
    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("setspawn")
                .aliases("spawnpoint")
                .permission("essentials.setspawn")
                .arguments(this.createArgumentInfo())
                .playerOnly(true)
                .build();
    }

    private ArgumentsDefinition createArgumentInfo() {
        return ArgumentsDefinition.builder()
                .optional("--center", ArgumentHandlers.STRING)
                .build();
    }
}
