package dev.oribuin.essentials.addon.teleport.command;

import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportConfig;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.oribuin.essentials.addon.teleport.model.TeleportRequest;
import dev.oribuin.essentials.util.StringPlaceholders;
import dev.oribuin.essentials.util.model.Cooldown;
import dev.oribuin.essentials.util.model.Placeholders;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TpAskCommand {

    private final TeleportAddon addon;
    private final Cooldown<UUID> cooldown;

    public TpAskCommand(TeleportAddon addon) {
        this.addon = addon;
        this.cooldown = new Cooldown<>();
    }

    /**
     * Teleport back to your previous location
     *
     * @param sender The sender who is running the command
     */
    @Command("tpa|tpask <target>")
    @Permission("essentials.tpask")
    @CommandDescription("Accept a teleport request from another player")
    public void execute(Player sender, Player target) {
        TeleportConfig config = TeleportConfig.getInstance();
        TeleportMessages messages = TeleportMessages.getInstance();

        // Check if the player has access to teleport to the world
        if (config.disableInaccessibleTp() && !sender.hasPermission(addon.getPerm(target.getWorld().getName()))) {
            messages.getDisabledWorld().send(sender);
            return;
        }

        // Check if sending to self
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            messages.getTeleportAskSelf().send(sender);
            return;
        }

        Duration cooldown = config.getTeleportCooldown();

        // Check if the player is on cooldown, ignore cooldown if they have a specific perm (disabled default)
        if (!cooldown.isZero() && !sender.hasPermission("essentials.tpa.bypass.cooldown")) {

            if (this.cooldown.onCooldown(sender.getUniqueId())) {
                long remaining = this.cooldown.getDurationRemaining(sender.getUniqueId()).getSeconds();
                messages.getTeleportCooldown().send(sender, "time", remaining);
                return;
            }

            this.cooldown.setCooldown(sender.getUniqueId(), cooldown);
        }

        // Cancel the current outgoing teleport request
        TeleportRequest outgoing = addon.getOutgoing(sender.getUniqueId());
        if (outgoing != null) {
            addon.requests().remove(outgoing);
        }

        TeleportRequest request = new TeleportRequest(
                sender.getUniqueId(),
                target.getUniqueId(),
                System.currentTimeMillis()
        );

        addon.requests().add(request);
        StringPlaceholders placeholders = Placeholders.of("target", target.getName(), "sender", sender.getName());
        messages.getTeleportAskReceived().send(target, placeholders);
        messages.getTeleportAskSelf().send(sender, placeholders);

        // Send the timeout message after several seconds
        this.addon.getScheduler().runTaskLater(() -> {
            if (addon.requests().remove(request)) {
                messages.getTeleportTimeout().send(sender, placeholders);
                messages.getTeleportTimeoutOther().send(target, placeholders);
            }
        }, config.getRequestTimeout().toSeconds(), TimeUnit.SECONDS);
    }

}
