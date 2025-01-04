package dev.oribuin.essentials.addon.home.command;

import com.destroystokyo.paper.ParticleBuilder;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.home.HomeAddon;
import dev.oribuin.essentials.command.argument.HomeArgumentHandler;
import dev.oribuin.essentials.addon.home.config.HomeConfig;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.model.Home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HomeTPCommand extends BaseRoseCommand {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public HomeTPCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player sender = (Player) context.getSender();
        Home home = context.get("home");

        HomeAddon addon = EssentialsPlugin.getModule(HomeAddon.class);
        if (addon == null || !addon.enabled()) return;

        HomeConfig config = addon.config(HomeConfig.class);
        HomeMessages msgConfig = addon.config(HomeMessages.class);
        if (config == null || msgConfig == null) return;

        // Check if the world is disabled
        List<String> disabledWorlds = HomeConfig.DISABLED_WORLDS.getOr(config, new ArrayList<>()).asStringList();
        if (disabledWorlds.contains(home.location().getWorld().getName())) {
            HomeMessages.DISABLED_WORLD.send(msgConfig, sender);
            return;
        }

        // Number values are defaulted to 0 when not found
        int cooldown = HomeConfig.TP_COOLDOWN.get(config).asInt();
        int teleportDelay = HomeConfig.TP_DELAY.get(config).asInt();
        double cost = HomeConfig.TP_COST.get(config).asDouble();

        // Check if the player is on cooldown
        if (cooldown > 0) {
            long lastTeleport = this.cooldowns.getOrDefault(sender.getUniqueId(), 0L);
            long timeLeft = (lastTeleport + (cooldown * 1000L) - System.currentTimeMillis()) / 1000L;

            // Player is still on cooldown :3
            if (timeLeft > 0) {
                HomeMessages.HOME_COOLDOWN.send(msgConfig, sender, StringPlaceholders.of("time", timeLeft + "s"));
                return;
            }

            this.cooldowns.put(sender.getUniqueId(), System.currentTimeMillis());
        }

        // TODO: Check if has enough money
        if (cost > 0.0) {
            sender.sendMessage("You have been charged $" + cost + " to teleport to your home.");
        }

        // Create the tp effects task
        ScheduledTask effectTask = null;
        if (HomeConfig.TP_EFFECTS.get(config).asBoolean() && teleportDelay > 0) {
            // Give the player blindness
            sender.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                    teleportDelay * 20 - 10, 4,
                    false,
                    false,
                    false
            ));

            ParticleBuilder particle = new ParticleBuilder(Particle.WITCH)
                    .location(sender.getLocation())
                    .count(10)
                    .offset(0.5, 0.5, 0.5)
                    .extra(0.1);

            effectTask = EssentialsPlugin.scheduler().runTaskTimerAsync(particle::spawn, 0, 250, TimeUnit.MILLISECONDS);
        }

        // Teleport the player to the location
        ScheduledTask finalTask = effectTask;
        EssentialsPlugin.scheduler().runTaskLater(() -> {
            if (finalTask != null) finalTask.cancel();

            sender.teleportAsync(home.location(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }, teleportDelay, TimeUnit.SECONDS);

    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("home")
                .descriptionKey("command-home")
                .permission("essentials.home")
                .playerOnly(true)
                .arguments(this.createArgumentsDefinition())
                .build();
    }

    private ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("home", new HomeArgumentHandler())
                .optional("target", ArgumentHandlers.OFFLINE_PLAYER)
                .build();
    }

}
