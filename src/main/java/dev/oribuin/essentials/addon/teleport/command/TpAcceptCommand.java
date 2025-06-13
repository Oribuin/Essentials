package dev.oribuin.essentials.addon.teleport.command;

import com.destroystokyo.paper.ParticleBuilder;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.home.config.HomeConfig;
import dev.oribuin.essentials.addon.home.config.HomeMessages;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportConfig;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.oribuin.essentials.addon.teleport.model.TeleportRequest;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class TpAcceptCommand extends BaseRoseCommand {

    public TpAcceptCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        TeleportAddon addon = AddonProvider.TELEPORT_ADDON;
        Player target = (Player) context.getSender(); // a bit confusing but target = person accepting the teleport request to them

        // Cancel the current outgoing teleport request
        TeleportRequest incoming = addon.getIncoming(target.getUniqueId());
        if (incoming == null) {
            TeleportMessages.TELEPORT_INVALID.send(target);
            return;
        }

        // Check if the player has access to teleport to the world
        Player sender = Bukkit.getPlayer(incoming.sender());
        if (sender == null || !sender.isOnline()) {
            TeleportMessages.TELEPORT_INVALID.send(target);
            return;
        }

        // Check if the sender is allowed to teleport to the player
        if (!sender.hasPermission(addon.getPerm(target.getWorld().getName()))) {
            TeleportMessages.DISABLED_WORLD.send(target);
            return;
        }

        // important values
        int teleportDelay = TeleportConfig.TP_DELAY.getValue();
        double cost = TeleportConfig.TP_COST.getValue();
        StringPlaceholders placeholders = StringPlaceholders.of(
                "target", target.getName(),
                "sender", sender.getName(),
                "cost", cost,
                "delay", teleportDelay
        );

        addon.getRequests().remove(incoming);

        Location location = incoming.where() != null ? incoming.where() : target.getLocation();

        // Check if the location is safe to teleport to TODO //  Implement FinePosition#isSafe or something to that effect
        if (!Home.isSafe(location) && !sender.hasPermission("essentials.tpa.bypass.unsafe")) {
            TeleportMessages.TELEPORT_UNSAFE.send(sender, placeholders);
            return;
        }

        // Only run this if the cost is > 0 and bypass is enabled
        if (cost > 0.0 && !sender.hasPermission("essentials.tpa.bypass.cost")) {
            // check if they have enough
            if (!VaultProvider.get().has(sender, cost)) {
                TeleportMessages.INSUFFICIENT_FUNDS.send(sender, placeholders);
                return;
            }
        }

        // If the player has permission to bypass the delay, skip all effects
        if (sender.hasPermission("essentials.tpa.bypass.delay") || teleportDelay <= 0) {
            // send the final message
            TeleportMessages.TELEPORT_ACCEPT_SELF.send(target, placeholders);
            TeleportMessages.TELEPORT_ACCEPT_OTHER.send(sender, placeholders);

            // TELEPORT EFFECTS WOOO!!!!!!!!!!!
            this.teleport(sender, location, cost, placeholders);
            return;
        }

        // Create the tp effects task
        ScheduledTask effectTask = null;
        if (HomeConfig.TP_EFFECTS.getValue()) {
            // Give the player blindness
            sender.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                    (teleportDelay + 1) * 20, 4,
                    false,
                    false,
                    false
            ));

            ParticleBuilder particle = new ParticleBuilder(Particle.WITCH)
                    .count(10)
                    .offset(0.5, 0.5, 0.5)
                    .extra(0.1);

            effectTask = EssentialsPlugin.scheduler().runTaskTimerAsync(() ->
                            particle.location(sender.getLocation()).spawn(),
                    0, 250, TimeUnit.MILLISECONDS
            );
        }

        // send the final message
        TeleportMessages.TELEPORT_ACCEPT_SELF.send(target, placeholders);
        TeleportMessages.TELEPORT_ACCEPT_OTHER.send(sender, placeholders);
        
        // Teleport the player to the location
        ScheduledTask finalTask = effectTask;
        EssentialsPlugin.scheduler().runTaskLater(() -> {
            if (finalTask != null) finalTask.cancel();

            this.teleport(sender, location, cost, placeholders);
        }, teleportDelay, TimeUnit.SECONDS);
    }

    /**
     * Actually teleport the player to the other player and run all the associated functionalities <3
     *
     * @param player       The player to teleport
     * @param target       The target to teleport to
     * @param cost         How much it cost to teleport the player
     * @param placeholders The placeholders for messages
     */
    private void teleport(Player player, Location target, double cost, StringPlaceholders placeholders) {
        player.teleportAsync(target, PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(result -> {
            // check if teleport failed
            if (!result) {
                TeleportMessages.TELEPORT_FAILED.send(player, placeholders);
                return;
            }

            // Take away the money from the player.
            if (cost > 0) {
                VaultProvider.get().take(player, cost);
                TeleportMessages.TELEPORT_COST.send(player, placeholders);
            }
        });
    }


    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("tpaccept")
                .aliases("tpyes")
                .permission("essentials.tpaccept")
                .playerOnly(true)
                .build();
    }

}
