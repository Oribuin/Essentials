package dev.oribuin.essentials.command.impl;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.command.argument.AddonArgumentHandler;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;

public class ReloadCommand extends BaseRoseCommand {

    public ReloadCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Addon addon) {
        if (addon != null) {
            AddonProvider.unload(addon);
            AddonProvider.register(addon);
            context.getSender().sendMessage("Reloaded the addon: " + addon.name());
            return;
        }

        this.rosePlugin.reload(); // Reload the entire plugin
        context.getSender().sendMessage("Reloaded the plugin");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("essreload")
                .arguments(ArgumentsDefinition.builder()
                        .optional("addon", new AddonArgumentHandler())
                        .build()
                )
                .descriptionKey("command-reload-description")
                .permission("essentials.reload")
                .build();
    }
}
