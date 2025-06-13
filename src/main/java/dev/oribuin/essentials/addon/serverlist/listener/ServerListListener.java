package dev.oribuin.essentials.addon.serverlist.listener;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.serverlist.config.ServerListConfig;
import dev.oribuin.essentials.hook.plugin.PAPIProvider;
import dev.oribuin.essentials.util.EssUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import javax.swing.*;
import java.util.List;

@SuppressWarnings("deprecation")
public class ServerListListener implements Listener {

    /**
     * Handle the server list ping event to modify the motd and max players.
     *
     * @param event The event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onPing(ServerListPingEvent event) {
        event.setMaxPlayers(ServerListConfig.MAX_PLAYERS.getValue());

        List<String> motd = ServerListConfig.LINES.getValue();
        if (!motd.isEmpty()) {
            Component text = EssUtils.kyorify(PAPIProvider.apply(null, String.join("\n<reset><!italic>", motd)));
            event.motd(text);
        }
        
        List<CachedServerIcon> serverIcons = AddonProvider.SERVER_LIST_ADDON.icons();
        if (!serverIcons.isEmpty()) {
            CachedServerIcon random = serverIcons.get((int) (Math.random() * serverIcons.size()));
            if (random == null) return;
            event.setServerIcon(random);
        }

        // TODO: Allow for hoverable text in the motd
        // TODO: Allow for custom server icon
        // TODO: Allow for multiple motd options 
    }

}
