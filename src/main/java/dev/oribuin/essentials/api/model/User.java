package dev.oribuin.essentials.api.model;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User implements Audience {
    
    private final UUID uuid;
    private String name;
    private long firstJoin;
    
    /**
     * Create a new cached user for the database
     *
     * @param uuid      The uuid of the user
     * @param name      The name of the user
     * @param firstJoin The first time they join the server
     */
    public User(UUID uuid, String name, long firstJoin) {
        this.uuid = uuid;
        this.name = name;
        this.firstJoin = firstJoin;
    }

    /**
     * Create a new cached user for the database
     *
     * @param player    The player
     * @param firstJoin The first time they join the server
     */
    public User(Player player, long firstJoin) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.firstJoin = firstJoin;
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public long firstJoin() {
        return firstJoin;
    }

    public void firstJoin(long firstJoin) {
        this.firstJoin = firstJoin;
    }
}
