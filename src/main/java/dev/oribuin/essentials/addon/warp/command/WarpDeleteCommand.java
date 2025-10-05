package dev.oribuin.essentials.addon.warp.command;

import dev.oribuin.essentials.addon.warp.WarpsAddon;
import dev.oribuin.essentials.addon.warp.config.WarpMessages;
import dev.oribuin.essentials.addon.warp.model.Warp;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class WarpDeleteCommand implements AddonCommand {

    private final WarpsAddon addon;

    public WarpDeleteCommand(WarpsAddon addon) {
        this.addon = addon;
    }

    /**
     * Delete a user's warp
     *
     * @param sender The sender who is running the command
     * @param warp   The target of the command
     */
    @Command("delwarp|removewarp|unsetwarp <warp>")
    @Permission("essentials.warp.delete")
    @CommandDescription("Delete a user's warp ")
    public void execute(CommandSender sender, Warp warp) {
        WarpMessages messages = WarpMessages.getInstance();
        
        // Set the warp
        this.addon.repository().delete(warp);
        messages.getWarpDeleted().send(sender, "name", warp.name());
    }

}
