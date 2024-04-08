package xyz.oribuin.essentials.module.home.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.essentials.Essentials;
import xyz.oribuin.essentials.module.home.HomeModule;
import xyz.oribuin.essentials.command.argument.HomeArgumentHandler;
import xyz.oribuin.essentials.module.home.config.HomeConfig;
import xyz.oribuin.essentials.module.home.config.HomeMessages;
import xyz.oribuin.essentials.module.home.model.Home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomeTPCommand extends BaseRoseCommand {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public HomeTPCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player sender = (Player) context.getSender();
        Home home = context.get("home");

        HomeModule module = Essentials.getModule(HomeModule.class);
        if (module == null || !module.isEnabled()) return;

        HomeConfig config = module.config(HomeConfig.class);
        HomeMessages msgConfig = module.config(HomeMessages.class);
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
        BukkitTask effectTask;
        if (HomeConfig.TP_EFFECTS.get(config).asBoolean() && teleportDelay > 0) {
            // Give the player blindness
            sender.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                    teleportDelay * 20 - 10, 4,
                    false,
                    false,
                    false
            ));

            effectTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Essentials.get(), () -> sender.spawnParticle(
                    Particle.SPELL_WITCH,
                    sender.getLocation(),
                    10,
                    0.5, 0.5, 0.5,
                    0.1
            ), 0L, 5L);
        } else {
            effectTask = null;
        }

        // Teleport the player to the location
        Bukkit.getScheduler().runTaskLater(Essentials.get(), () -> {
            if (effectTask != null)
                effectTask.cancel();

            PaperLib.teleportAsync(sender, home.location(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }, teleportDelay * 20L);

    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("home")
                .descriptionKey("command-home")
                .permission("essentials.home")
                .playerOnly(true)
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("home", new HomeArgumentHandler())
                .optional("target", ArgumentHandlers.OFFLINE_PLAYER)
                .build();
    }

}
