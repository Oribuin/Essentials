package dev.oribuin.essentials.addon.basic.command.gamemode.impl;

import dev.oribuin.essentials.addon.basic.command.gamemode.GamemodeTemplate;
import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.GameMode;

/**
 * Create a new Survival Gamemode Template Command as a shorthand
 */
public class SurvivalCommand extends GamemodeTemplate {

    /**
     * Initialise the gamemode command with all the gamemodes
     *
     * @param rosePlugin The plugin instance
     */
    public SurvivalCommand(RosePlugin rosePlugin) {
        super(rosePlugin, GameMode.SURVIVAL, "gms", "survival");
    }

}

