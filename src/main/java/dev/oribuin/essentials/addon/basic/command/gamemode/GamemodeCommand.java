package dev.oribuin.essentials.addon.basic.command.gamemode;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.basic.BasicAddon;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeCommand extends BaseRoseCommand {

    public GamemodeCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, GameMode gamemode, Player target) {
        // Swap the target if the sender does not have permission to view other player's ping
        if (target != null && !context.getSender().hasPermission("essentials.gamemode.others") && context.getSender() instanceof Player sender) {
            target = sender;
        }

        String permission = "essentials.gamemode." + gamemode.name().toLowerCase();

        if (!context.getSender().hasPermission(permission)) {
            // todo: send no permission
            context.getSender().sendMessage("no permission to use this gamemode.");
            return;
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
        return CommandInfo.builder("gamemode")
                .permission("essentials.gamemode")
                .aliases("gm")
                .arguments(
                        ArgumentsDefinition.builder()
                                .required("gamemode", ArgumentHandlers.forEnum(GameMode.class))
                                .optional("target", ArgumentHandlers.PLAYER)
                                .build()
                )
                .build();
    }

    /**
     * Create a new Creative Gamemode Template Command as a shorthand
     */
    public static class CreativeCommand extends GamemodeTemplate {
        public CreativeCommand(RosePlugin rosePlugin) {
            super(rosePlugin, GameMode.CREATIVE, "gmc", "creative");
        }
    }

    /**
     * Create a new Survival Gamemode Template Command as a shorthand
     */
    public static class SurvivalCommand extends GamemodeTemplate {
        public SurvivalCommand(RosePlugin rosePlugin) {
            super(rosePlugin, GameMode.SURVIVAL, "gms", "survival");
        }
    }

    /**
     * Create a new Adventure Gamemode Template Command as a shorthand
     */
    public static class AdventureCommand extends GamemodeTemplate {
        public AdventureCommand(RosePlugin rosePlugin) {
            super(rosePlugin, GameMode.ADVENTURE, "gma", "adventure");
        }
    }

    /**
     * Create a new Spectator Gamemode Template Command as a shorthand
     */
    public static class SpectatorCommand extends GamemodeTemplate {
        public SpectatorCommand(RosePlugin rosePlugin) {
            super(rosePlugin, GameMode.SPECTATOR, "gmsp", "spectator");
        }
    }

}
