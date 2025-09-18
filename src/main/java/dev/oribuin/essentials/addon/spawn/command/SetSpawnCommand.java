package dev.oribuin.essentials.addon.spawn.command;

import dev.oribuin.essentials.addon.spawn.SpawnAddon;
import dev.oribuin.essentials.addon.spawn.config.SpawnConfig;
import dev.oribuin.essentials.addon.spawn.config.SpawnMessages;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.util.NumberUtil;
import dev.oribuin.essentials.util.model.FinePosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class SetSpawnCommand implements AddonCommand {

    private final SpawnAddon addon;

    public SetSpawnCommand(SpawnAddon addon) {
        this.addon = addon;
    }

    /**
     * Feed a player and heal their saturation levels
     *
     * @param sender The sender who is running the command
     */
    @Command("setspawn")
    @Permission("essentials.setspawn")
    @CommandDescription("Set the server spawn")
    public void execute(Player sender) {
        SpawnConfig config = SpawnConfig.getInstance();
        SpawnMessages messages = SpawnMessages.getInstance();

        Location location = sender.getLocation().clone();
        //        if (center != null) {
        //            location = location.toCenterLocation();
        //        }


        config.setSpawnpoint(FinePosition.from(location));
        location.getWorld().setSpawnLocation(location);
        this.addon.getConfigLoader().saveConfig(SpawnConfig.class);
        messages.getSetSpawn().send(sender,
                "x", NumberUtil.rounded(location.getX(), 3),
                "y", NumberUtil.rounded(location.getY(), 3),
                "z", NumberUtil.rounded(location.getZ(), 3)
        );
    }

}
