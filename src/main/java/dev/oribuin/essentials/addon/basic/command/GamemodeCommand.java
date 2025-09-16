package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.basic.BasicAddon;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.config.TextMessage;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;

@SuppressWarnings("UnstableApiUsage")
public class GamemodeCommand {

    private final BasicAddon addon;

    public GamemodeCommand(BasicAddon addon) {
        this.addon = addon;
    }

    /**
     * Register all the gamemode commands
     */
    public void register() {
        LegacyPaperCommandManager<CommandSender> manager = this.addon.plugin().getCommandManager();
        manager.command(this.from(GameMode.ADVENTURE, "gma", "adventure"));
        manager.command(this.from(GameMode.CREATIVE, "gmc", "creative"));
        manager.command(this.from(GameMode.SPECTATOR, "gmsp", "spectator"));
        manager.command(this.from(GameMode.SURVIVAL, "gms", "survival"));

        this.addon.plugin().getParser().parse(this);
    }

    /**
     * Command for changing the player's gamemode to a specified one
     *
     * @param sender   The command executor
     * @param gamemode The gamemode to change to
     * @param target   The target having their gamemode changed
     */
    @org.incendo.cloud.annotations.Command("gamemode|gm <gamemode> [target]")
    @Permission("essentials.gamemode")
    @CommandDescription("Change your current gamemode")
    public void execute(CommandSender sender, GameMode gamemode, Player target) {
        BasicMessages messages = BasicMessages.get();

        // Swap the target if the sender does not have permission to view other player's ping
        if (target != null && !sender.hasPermission("essentials.gamemode.others") && sender instanceof Player playerSender) {
            target = playerSender;
        }

        // Check if sender has permission to use the gamemode
        String permission = "essentials.gamemode." + gamemode.name().toLowerCase();
        if (!sender.hasPermission(permission)) {
            EssentialsPlugin.getMessages().getNoPermission().send(sender);
            return;
        }

        Player focus = target != null ? target : (Player) sender;
        TextMessage message = target != null ? messages.getChangeGamemodeOther() : messages.getChangeGamemode();
        this.addon.getScheduler().runTask(() -> focus.setGameMode(gamemode));
        message.send(sender,
                "gamemode", StringUtils.capitalize(gamemode.name().toLowerCase()),
                "target", focus.getName()
        );
    }

    /**
     * Create specific gamemode commands for the plugin
     *
     * @param gamemode The gamemode to apply the command to
     * @param name     The name of the command
     * @param aliases  Any possible command aliases
     *
     * @return The returning command
     */
    public Command<CommandSender> from(GameMode gamemode, String name, String... aliases) {
        return this.addon.plugin().getCommandManager().commandBuilder(name, aliases)
                .permission("essentials.gamemode." + gamemode.name().toLowerCase())
                .optional("target", PlayerParser.playerParser())
                .handler(context -> {
                    CommandSender sender = context.sender();
                    Player target = context.getOrDefault("target", null);
                    BasicMessages messages = BasicMessages.get();

                    // Swap the target if the sender does not have permission to view other player's ping
                    if (target != null && !sender.hasPermission("essentials.gamemode.others") && sender instanceof Player playerSender) {
                        target = playerSender;
                    }

                    // Apply the gamemode changes
                    Player focus = target != null ? target : (Player) sender;
                    TextMessage message = target != null ? messages.getChangeGamemodeOther() : messages.getChangeGamemode();
                    this.addon.getScheduler().runTask(() -> focus.setGameMode(gamemode));
                    message.send(sender, "target", focus.getName(), "gamemode", StringUtils.capitalize(gamemode.name().toLowerCase()));
                })
                .build();
    }

}
