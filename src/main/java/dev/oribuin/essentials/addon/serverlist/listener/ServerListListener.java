package dev.oribuin.essentials.addon.serverlist.listener;

import dev.oribuin.essentials.addon.serverlist.ServerListAddon;
import dev.oribuin.essentials.addon.serverlist.config.ServerListConfig;
import dev.oribuin.essentials.hook.plugin.PAPIProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.List;

@SuppressWarnings("deprecation")
public class ServerListListener implements Listener {

    private final ServerListAddon addon;

    /**
     * Construct a new listener with the given addon.
     *
     * @param addon The addon instance
     */
    public ServerListListener(ServerListAddon addon) {
        this.addon = addon;
    }

    /**
     * Handle the server list ping event to modify the motd and max players.
     *
     * @param event The event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onPing(ServerListPingEvent event) {
        ServerListConfig config = this.addon.config(ServerListConfig.class);
        if (config == null) return;

        event.setMaxPlayers(ServerListConfig.MAX_PLAYERS.getValue());

        List<String> motd = ServerListConfig.LINES.getValue();
        if (motd.isEmpty()) return;

        event.setMotd(PAPIProvider.apply(null, String.join("\n", motd)));

        // TODO: Allow for hoverable text in the motd
        // TODO: Allow for custom server icon
        // TODO: Allow for multiple motd options 
    }

}
