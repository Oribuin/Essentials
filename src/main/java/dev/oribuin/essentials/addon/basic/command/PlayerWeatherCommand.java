package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.util.model.Weather;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class PlayerWeatherCommand {

    /**
     * Change your current player weather
     *
     * @param sender  The sender who is running the command
     * @param weather The target weather
     */
    @Command("pweather|epweather <weather>")
    @Permission("essentials.pweather")
    @CommandDescription("Change your current player weather")
    public void execute(Player sender, Weather weather) {
        BasicMessages messages = BasicMessages.get();
        
        weather.apply(sender);
        messages.getPlayerWeather().send(
                sender,
                "weather", StringUtils.capitalize(weather.name().toLowerCase())
        );
    }

    /**
     * Change your current player weather
     *
     * @param sender  The sender who is running the command
     * @param weather The target weather
     */
    @Command("pweather|epweather <weather> <target>")
    @Permission("essentials.pweather.others")
    @CommandDescription("Change your current player weather")
    public void executeTarget(CommandSender sender, Weather weather, Player target) {
        BasicMessages messages = BasicMessages.get();
        weather.apply(target);
        messages.getPlayerWeatherOther().send(
                sender,
                "weather", StringUtils.capitalize(weather.name().toLowerCase())
        );
    }

}
