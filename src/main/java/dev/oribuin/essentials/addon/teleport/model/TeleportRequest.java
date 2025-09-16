package dev.oribuin.essentials.addon.teleport.model;

import dev.oribuin.essentials.addon.teleport.config.TeleportConfig;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Create a new Teleport Request to handle the
 */
public final class TeleportRequest {

    private final UUID sender;
    private final UUID target;
    private final long when;
    private Location where;

    /**
     * Create a new Teleport Request to handle the
     *
     * @param sender The player who sent the request
     * @param target The player who received the request
     * @param when   When the request was sent
     * @param where  The place to teleport to
     */
    public TeleportRequest(UUID sender, UUID target, long when, Location where) {
        this.sender = sender;
        this.target = target;
        this.when = when;
        this.where = where;
    }

    /**
     * Create a new Teleport Request to handle the
     *
     * @param sender The player who sent the request
     * @param target The player who received the request
     * @param when   When the request was sent
     */
    public TeleportRequest(UUID sender, UUID target, long when) {
        this(sender, target, when, null);
    }

    /**
     * Check if the teleport request has expired
     *
     * @return The teleport request
     */
    public boolean hasExpired() {
        return System.currentTimeMillis() - when > TeleportConfig.getInstance().getRequestTimeout().toMillis() * 1000;
    }

    /**
     * Check if a user is the target of the teleport request
     *
     * @param user The user to check
     *
     * @return true if they're the target
     */
    public boolean isTarget(UUID user) {
        return this.target.equals(user);
    }

    /**
     * Check if a user is the original sender of the teleport request
     *
     * @param user The user to check
     *
     * @return true if they're the sender
     */
    public boolean isSender(UUID user) {
        return this.sender.equals(user);
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getTarget() {
        return target;
    }

    public long getWhen() {
        return when;
    }

    public Location getWhere() {
        return where;
    }

    public void setWhere(Location where) {
        this.where = where;
    }

    @Override
    public String toString() {
        return "TeleportRequest{" +
               "sender=" + sender +
               ", target=" + target +
               ", when=" + when +
               ", where=" + where +
               '}';
    }
}
