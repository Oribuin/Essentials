package dev.oribuin.essentials.addon.warp.event;

import dev.oribuin.essentials.addon.warp.model.Warp;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class WarpCreateEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Warp warp;
    private boolean cancelled = false;

    public WarpCreateEvent(@NotNull Player who, Warp warp) {
        super(who, !Bukkit.isPrimaryThread());
        this.warp = warp;
    }

    public Warp getWarp() {
        return warp;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
