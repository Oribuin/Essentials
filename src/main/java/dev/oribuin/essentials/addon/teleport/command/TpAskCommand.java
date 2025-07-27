package dev.oribuin.essentials.addon.teleport.command;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.addon.spawn.config.SpawnConfig;
import dev.oribuin.essentials.addon.spawn.config.SpawnMessages;
import dev.oribuin.essentials.addon.teleport.TeleportAddon;
import dev.oribuin.essentials.addon.teleport.config.TeleportConfig;
import dev.oribuin.essentials.addon.teleport.config.TeleportMessages;
import dev.oribuin.essentials.addon.teleport.model.TeleportRequest;
import dev.oribuin.essentials.util.Confirmation;
import dev.oribuin.essentials.util.EssUtils;
import dev.oribuin.essentials.util.Placeholders;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TpAskCommand extends BaseRoseCommand {
    
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public TpAskCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target) {
        TeleportAddon addon = AddonProvider.TELEPORT_ADDON;
        Player sender = (Player) context.getSender();

        // Check if the player has access to teleport to the world
        if (!sender.hasPermission(addon.getPerm(target.getWorld().getName()))) {
            TeleportMessages.DISABLED_WORLD.send(sender);
            return;
        }

        // Check if sending to self
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            TeleportMessages.TELEPORT_SELF.send(sender);
            return;
        }
        
        Duration cooldown = TeleportConfig.TP_COOLDOWN.value();

        // Check if the player is on cooldown, ignore cooldown if they have a specific perm (disabled default)
        if (!cooldown.isZero() && !sender.hasPermission("essentials.tpa.bypass.cooldown")) {
            long lastTeleport = this.cooldowns.getOrDefault(sender.getUniqueId(), 0L);
            long timeLeft = (lastTeleport + cooldown.toMillis() - System.currentTimeMillis()) / 1000L;

            // Player is still on cooldown :3
            if (timeLeft > 0) {
                TeleportMessages.TELEPORT_COOLDOWN.send(sender, "time", timeLeft + "s");
                return;
            }

            this.cooldowns.put(sender.getUniqueId(), System.currentTimeMillis());
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
        TeleportMessages.TELEPORT_ASK_RECEIVED.send(target, placeholders);
        TeleportMessages.TELEPORT_ASK_SENT.send(sender, placeholders);

        // Send the timeout message after several seconds
        EssentialsPlugin.scheduler().runTaskLater(() -> {
            if (addon.requests().remove(request)) {
                TeleportMessages.TELEPORT_TIMEOUT.send(sender, placeholders);
                TeleportMessages.TELEPORT_TIMEOUT_OTHER.send(target, placeholders);
            }
        }, TeleportConfig.REQUEST_TIMEOUT.value(), TimeUnit.SECONDS);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("tpa")
                .aliases("tpask")
                .permission("essentials.tpa")
                .arguments(EssUtils.createTarget(false))
                .playerOnly(true)
                .build();
    }

}
