package dev.oribuin.essentials.addon.basic;

import dev.oribuin.essentials.addon.Addon;
import dev.oribuin.essentials.addon.basic.command.ClearCommand;
import dev.oribuin.essentials.addon.basic.command.EnderchestCommand;
import dev.oribuin.essentials.addon.basic.command.FeedCommand;
import dev.oribuin.essentials.addon.basic.command.FlyCommand;
import dev.oribuin.essentials.addon.basic.command.GamemodeCommand;
import dev.oribuin.essentials.addon.basic.command.HealCommand;
import dev.oribuin.essentials.addon.basic.command.PingCommand;
import dev.oribuin.essentials.addon.basic.command.PlayerWeatherCommand;
import dev.oribuin.essentials.addon.basic.command.RepairCommand;
import dev.oribuin.essentials.addon.basic.command.TopCommand;
import dev.oribuin.essentials.addon.basic.command.TrashCommand;
import dev.oribuin.essentials.addon.basic.command.WeatherCommand;
import dev.oribuin.essentials.addon.basic.command.WhereAmICommand;
import dev.oribuin.essentials.addon.basic.config.BasicConfig;
import dev.oribuin.essentials.addon.basic.config.BasicMessages;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.config.AddonConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BasicAddon extends Addon {

    private static BasicAddon instance;

    /**
     * Create a new instance of the addon
     */
    public BasicAddon() {
        super("basic");

        instance = this;
    }

    /**
     * When the addon is finished loading and is ready to be used.
     */
    @Override
    public void enable() {
        // This command is wonky so we register it differently
        GamemodeCommand gamemodeCommand = new GamemodeCommand(this);
        gamemodeCommand.register();
    }

    /**
     * Get all the commands for the addon
     */
    @Override
    public List<AddonCommand> getCommands() {
        return List.of(
                new ClearCommand(),
                new EnderchestCommand(this),
                new FeedCommand(),
                new FlyCommand(),
                new HealCommand(),
                new PingCommand(),
                new PlayerWeatherCommand(),
                new RepairCommand(),
                new TopCommand(),
                new TrashCommand(this),
                new WeatherCommand(this),
                new WhereAmICommand(this)
        );
    }

    /**
     * Get all the listeners for the addon
     */
    @Override
    public List<Listener> getListeners() {
        return List.of(this);
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public Map<String, Supplier<AddonConfig>> getConfigs() {
        return Map.of(
                "config", BasicConfig::new,
                "messages", BasicMessages::new
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("essentials.fly") && player.hasPermission("essentials.fly.login")) {
            player.setAllowFlight(true);
        }
    }
    
    public static BasicAddon get() {
        return instance;
    }
}
