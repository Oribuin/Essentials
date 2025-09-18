package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.BasicAddon;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.util.model.Weather;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.time.Duration;

public class WeatherCommand implements AddonCommand {

    private final BasicAddon addon;

    public WeatherCommand(BasicAddon addon) {
        this.addon = addon;
    }

    /**
     * Change your current player weather
     *
     * @param sender  The sender who is running the command
     * @param weather The target weather
     */
    @Command("weather|eweather|setweather <weather> [duration]")
    @Permission("essentials.weather")
    @CommandDescription("Change your current player weather")
    public void execute(Player sender, Weather weather, Duration duration) {
        BasicMessages messages = BasicMessages.get();

        // Applies the weather state to the world 
        this.addon.getScheduler().runTask(() -> weather.apply(sender.getLocation(), duration));
        messages.getWeatherCommand().send(
                sender,
                "weather", StringUtils.capitalize(weather.name().toLowerCase())
        );
    }

}
