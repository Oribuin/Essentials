package dev.oribuin.essentials.command.impl;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.Addon;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class ReloadCommand implements AddonCommand {

    private final EssentialsPlugin plugin;

    public ReloadCommand(EssentialsPlugin plugin) {
        this.plugin = plugin;
    }

    @Command("essentials|ess reload [addon]")
    @Permission("essentials.reload")
    @CommandDescription("Reload the plugin Command")
    public void execute(CommandSender sender, Addon addon) {
        if (addon != null) {
            AddonProvider.unload(addon);
            AddonProvider.register(addon);
            sender.sendMessage("Reloaded the addon: " + addon.getClass().getSimpleName());
            return;
        }

        this.plugin.reload();
        sender.sendMessage("Reloaded the plugin");
    }

}
