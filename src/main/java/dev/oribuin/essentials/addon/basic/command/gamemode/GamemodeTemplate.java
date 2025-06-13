package dev.oribuin.essentials.addon.basic.command.gamemode;

import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public abstract class GamemodeTemplate extends BaseRoseCommand {

    protected final GameMode gamemode;
    private final String command;
    private final String[] aliases;

    public GamemodeTemplate(RosePlugin rosePlugin, GameMode gamemode, String command, String... aliases) {
        super(rosePlugin);
        this.gamemode = gamemode;
        this.command = command;
        this.aliases = aliases;
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target) {
        // Swap the target if the sender does not have permission to view other player's ping
        if (target != null && !context.getSender().hasPermission("essentials.gamemode.others") && context.getSender() instanceof Player sender) {
            target = sender;
        }

        Player focus = target != null ? target : (Player) context.getSender();
        if (target != null) {
            target.setGameMode(gamemode);
            BasicMessages.CHANGE_GAMEMODE_OTHER.send(context.getSender(),
                    "target", target.getName(),
                    "gamemode", StringUtils.capitalize(gamemode.name().toLowerCase())
            );
        } else {
            focus.setGameMode(gamemode);
            BasicMessages.CHANGE_GAMEMODE.send(context.getSender(),
                    "gamemode", StringUtils.capitalize(gamemode.name().toLowerCase())
            );
        }
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder(this.command)
                .aliases(this.aliases)
                .permission("essentials.gamemode." + this.gamemode.name().toLowerCase())
                .arguments(ArgumentsDefinition.builder()
                        .optional("target", ArgumentHandlers.PLAYER)
                        .build()
                )
                .build();
    }
}