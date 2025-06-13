package dev.oribuin.essentials.addon.basic.command.gamemode.impl;

import dev.oribuin.essentials.addon.basic.command.gamemode.GamemodeTemplate;
import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.GameMode;

/**
 * Create a new Adventure Gamemode Template Command as a shorthand
 */
public class AdventureCommand extends GamemodeTemplate {

    /**
     * Initialise the gamemode command with all the gamemodes
     *
     * @param rosePlugin The plugin instance
     */
    public AdventureCommand(RosePlugin rosePlugin) {
        super(rosePlugin, GameMode.ADVENTURE, "gma", "adventure");
    }

}

