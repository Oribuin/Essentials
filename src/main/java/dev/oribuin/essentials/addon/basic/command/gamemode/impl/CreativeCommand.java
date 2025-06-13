package dev.oribuin.essentials.addon.basic.command.gamemode.impl;

import dev.oribuin.essentials.addon.basic.command.gamemode.GamemodeTemplate;
import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.GameMode;

/**
 * Create a new Creative Gamemode Template Command as a shorthand
 */
public class CreativeCommand extends GamemodeTemplate {

    /**
     * Initialise the gamemode command with all the gamemodes
     *
     * @param rosePlugin The plugin instance
     */
    public CreativeCommand(RosePlugin rosePlugin) {
        super(rosePlugin, GameMode.CREATIVE, "gmc", "creative");
    }

}

