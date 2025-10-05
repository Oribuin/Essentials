package dev.oribuin.essentials.addon.warp.command;

import dev.oribuin.essentials.addon.warp.WarpsAddon;
import dev.oribuin.essentials.addon.warp.config.WarpConfig;
import dev.oribuin.essentials.addon.warp.config.WarpMessages;
import dev.oribuin.essentials.addon.warp.database.WarpRepository;
import dev.oribuin.essentials.addon.warp.event.WarpCreateEvent;
import dev.oribuin.essentials.addon.warp.model.Warp;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

public class WarpSetCommand implements AddonCommand {

    private final WarpsAddon addon;

    public WarpSetCommand(WarpsAddon addon) {
        this.addon = addon;
    }

    /**
     * Create a new user warp
     *
     * @param sender The sender who is running the command
     * @param name   The name of the new warp
     */
    @Command("setwarp|createwarp <warp>")
    @Permission("essentials.warp.create")
    @CommandDescription("Delete a user's warp ")
    public void execute(Player sender, @Argument("warp") String name) {
        WarpConfig config = WarpConfig.getInstance();
        WarpMessages messages = WarpMessages.getInstance();
        WarpRepository repository = this.addon.repository();

        // Check if the world is disabled
        if (config.getDisabledWorlds().contains(sender.getWorld().getName())) {
            messages.getDisabledWorld().send(sender);
            return;
        }
        
        // Check if a player has a warp by that name already
        if (repository.getWarp(name) != null) {
            messages.getWarpAlreadyExists().send(sender);
            return;
        }
        
        Warp warp = new Warp(name.toLowerCase(), sender.getLocation().toCenterLocation());
        WarpCreateEvent event = new WarpCreateEvent(sender, warp);
        event.callEvent();

        if (event.isCancelled()) return;

        // Set the warp
        repository.save(warp);
        messages.getWarpSet().send(sender, "name", warp.name());
    }

}
