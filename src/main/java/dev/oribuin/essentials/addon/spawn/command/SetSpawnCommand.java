package dev.oribuin.essentials.addon.spawn.command;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.spawn.config.SpawnConfig;
import dev.oribuin.essentials.addon.spawn.config.SpawnMessages;
import dev.oribuin.essentials.util.FinePosition;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

        SpawnConfig.SPAWNPOINT.value(FinePosition.from(location));
        AddonProvider.SPAWN_ADDON.config().save();
        SpawnMessages.SET_SPAWN.send(sender, "x", location.getX(), "y", location.getY(), "z", location.getZ());
    }

    /**
     * @return a newly constructed {@link CommandInfo} for this command
     */
    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("setspawn")
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
