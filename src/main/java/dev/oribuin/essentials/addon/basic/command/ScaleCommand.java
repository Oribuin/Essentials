package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.AddonCommand;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class ScaleCommand implements AddonCommand {

    /**
     * Modify the scale of your player model
     *
     * @param sender The sender who is running the command
     */
    @Command("scale reset")
    @Permission("essentials.scale")
    @CommandDescription("Modify the scale of an entity to")
    public void rescaleResetSelf(Player sender) {
        BasicMessages messages = BasicMessages.get();

        AttributeInstance attribute = sender.getAttribute(Attribute.SCALE);
        if (attribute == null) return;

        attribute.setBaseValue(attribute.getDefaultValue());
        messages.getRescaleSelf().send(sender, "scale", attribute.getDefaultValue());
    }
    
    /**
     * Modify the scale of your player model
     *
     * @param sender The sender who is running the command
     * @param scale The new size to set the player to
     */
    @Command("scale <scale>")
    @Permission("essentials.scale")
    @CommandDescription("Modify the scale of an entity to")
    public void rescaleSelf(Player sender, double scale) {
        BasicMessages messages = BasicMessages.get();

        AttributeInstance attribute = sender.getAttribute(Attribute.SCALE);
        if (attribute == null) return;
        
        attribute.setBaseValue(scale);
        messages.getRescaleSelf().send(sender, "scale", scale);
    }

    /**
     * Modify the scale of another player's character model
     *
     * @param sender The sender who is running the command
     * @param scale The new size to set the player to
     * @param target The target of the scale
     */
    @Command("scale <scale> <target>")
    @Permission("essentials.scale.others")
    @CommandDescription("Modify the scale of another player's character model")
    public void rescaleOther(CommandSender sender, double scale, Player target) {
        BasicMessages messages = BasicMessages.get();

        AttributeInstance attribute = target.getAttribute(Attribute.SCALE);
        if (attribute == null) return;

        attribute.setBaseValue(scale);
        messages.getRescaleOther().send(sender, "scale", scale, "target", target.getName());
    }
    
    /**
     * Modify the scale of another player's character model
     *
     * @param sender The sender who is running the command
     * @param scale The new size to set the player to
     * @param target The target of the scale
     */
    @Command("scale reset <target>")
    @Permission("essentials.scale.others")
    @CommandDescription("Modify the scale of another player's character model")
    public void resetRescaleOther(CommandSender sender, Player target) {
        BasicMessages messages = BasicMessages.get();

        AttributeInstance attribute = target.getAttribute(Attribute.SCALE);
        if (attribute == null) return;

        attribute.setBaseValue(attribute.getDefaultValue());
        messages.getRescaleOther().send(sender, "scale", attribute.getDefaultValue(), "target", target.getName());
    }
    
}
