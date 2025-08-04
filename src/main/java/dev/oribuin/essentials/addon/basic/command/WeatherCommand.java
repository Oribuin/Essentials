package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.util.Weather;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

public class WeatherCommand extends BaseRoseCommand {

    public WeatherCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Weather weather, Integer duration) {
        Player player = (Player) context.getSender();
        weather.apply(player.getLocation(), duration);

        BasicMessages.WEATHER_COMMAND.send(player,
                "weather", StringUtils.capitalize(weather.name().toLowerCase())
        );
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("weather")
                .permission("essentials.weather")
                .aliases("eweather", "setweather")
                .playerOnly(true)
                .arguments(
                        ArgumentsDefinition.builder()
                                .required("weather", ArgumentHandlers.forEnum(Weather.class))
                                .optional("duration", ArgumentHandlers.INTEGER)
                                .build()
                )
                .build();
    }

}
