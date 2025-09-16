package dev.oribuin.essentials.addon.serverlist.listener;

import dev.oribuin.essentials.addon.serverlist.ServerListAddon;
import dev.oribuin.essentials.addon.serverlist.config.ServerListConfig;
import dev.oribuin.essentials.util.EssUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import java.util.List;

public class ServerListListener implements Listener {

    private final ServerListAddon addon;

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
        ServerListConfig config = ServerListConfig.getInstance();
        event.setMaxPlayers(config.getMaxPlayers());

        List<ServerListConfig.Lines> lines = ServerListConfig.getInstance().getLines();
        if (!lines.isEmpty()) {
            ServerListConfig.Lines random = lines.get((int) (Math.random() * lines.size()));
            Component component = Component.empty();
            component = component.append(random.getFirstLine().parse(random.getFirstLine().message()));
            component = component.append(EssUtils.kyorify("\n<reset><!italic>"));
            component = component.append(random.getSecondLine().parse(random.getSecondLine().message()));

            event.motd(component);
        }


        List<CachedServerIcon> serverIcons = this.addon.icons();
        if (!serverIcons.isEmpty()) {
            CachedServerIcon random = serverIcons.get((int) (Math.random() * serverIcons.size()));
            if (random == null) return;
            event.setServerIcon(random);
        }
    }

}
