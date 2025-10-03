package dev.oribuin.essentials.addon.chat.renderer;

import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.addon.chat.channel.ChatChannels;
import dev.oribuin.essentials.addon.chat.database.ChatRepository;
import dev.oribuin.essentials.addon.chat.database.ChatSender;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private final ChatAddon addon;
    private final ChatAddonRenderer renderer;
    private final Pattern uppercase = Pattern.compile("[A-Z]");
    private final Pattern item = Pattern.compile("<item>");

    public ChatListener(ChatAddon addon) {
        this.addon = addon;
        this.renderer = new ChatAddonRenderer(addon);
    }

    /**
     * Render out the chat format for players
     *
     * @param event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String plain = ChatAddonRenderer.PLAIN.serialize(event.message());
        ChatSender sender = this.addon.getRepository().get(player.getUniqueId());
        Audience audience = sender.getChannel().getAudience(player);
        if (audience == null) {
            audience = Bukkit.getServer();
            sender.setChannel(ChatChannels.GLOBAL);
        }
        
        event.viewers().clear();
        event.viewers().add(audience);
        event.setCancelled(true);
        Component format = ChatAddonRenderer.render(player, sender, event.message());

        // todo: caps check but wgaf
        if (plain.contains("<item>") && player.hasPermission("essentials.chat.item")) {
            // todo: have clicking on item open it in gui
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (mainHand.getType().isAir()) {
                player.sendMessage("You need to be holding an item to do this"); // todo: config message
                return;
            }

            format = format.replaceText(x -> x.match(item).times(1)
                    .replacement(mainHand.displayName())
                    .build());
        }

        Component finalFormat = format;
        event.viewers().forEach(x -> x.sendMessage(finalFormat));
    }

    /**
     * Load the user's chat data when they login to the server
     *
     * @param event The player join event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        ChatRepository repository = this.addon.getRepository();
        UUID uuid = event.getPlayer().getUniqueId();

        repository.getAsync(uuid).thenAccept(sender -> repository.getUsers().put(uuid, sender));
    }

    /**
     * Remove the user's data from the cache and save it when they leave
     *
     * @param event The player quit event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        ChatRepository repository = this.addon.getRepository();
        UUID uuid = event.getPlayer().getUniqueId();

        ChatSender sender = repository.getUsers().remove(uuid);
        if (sender != null) {
            this.addon.getScheduler().runTaskAsync(() -> repository.saveUser(sender));
        }
    }

}

