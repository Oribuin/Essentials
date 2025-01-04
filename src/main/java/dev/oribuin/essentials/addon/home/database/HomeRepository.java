package dev.oribuin.essentials.addon.home.database;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.api.database.ModuleRepository;
import dev.oribuin.essentials.api.database.StatementProvider;
import dev.oribuin.essentials.api.database.StatementType;
import dev.oribuin.essentials.api.database.serializer.DataType;
import dev.oribuin.essentials.api.database.serializer.def.DataTypes;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomeRepository extends ModuleRepository implements Listener {

    private final Map<UUID, List<Home>> homes = new HashMap<>();

    public HomeRepository(DatabaseConnector connector) {
        super(connector, "homes");
    }

    /**
     * Load all the homes for a player from the database
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        this.load(event.getPlayer().getUniqueId());
    }

    /**
     * Remove all the homes for a player from the cache
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.unload(event.getPlayer().getUniqueId());
    }

    /**
     * Create the table for the homes in the database
     */
    public void establishTables() {
        this.connector.connect(x -> StatementProvider.create(StatementType.CREATE_TABLE, x)
                .table(this.table)
                .column("owner", DataTypes.UUID)
                .column("name", DataTypes.STRING)
                .column("location", DataTypes.LOCATION, false)
                .primary("owner", "name")
                .execute()
        );
    }

    /**
     * Load all the homes for a player from the database
     *
     * @param owner The owner of the homes
     */
    public void load(UUID owner) {
        
        this.connector.connect(x -> StatementProvider.create(StatementType.SELECT, x)
                .table(this.table)
                .column("owner", DataTypes.UUID, owner)
                .execute()
                .thenAcceptAsync(resultSet -> {
                    if (resultSet == null) return;

                    try {
                        List<Home> homes = new ArrayList<>();
                        while (resultSet.next()) {
                            homes.add(Home.construct(resultSet));
                        }

                        this.homes.put(owner, homes);
                    } catch (Exception ex) {
                        EssentialsPlugin.get().getLogger().severe("[HomeModule] Error while loading homes for " + owner.toString());
                        EssentialsPlugin.get().getLogger().severe(ex.getMessage());
                    }
                }));
    }

    /**
     * Remove all the homes for a player from the cache
     *
     * @param owner The owner of the homes
     */
    public void unload(UUID owner) {
        this.homes.remove(owner);
    }

    /**
     * Save a home to the database
     *
     * @param home The home to save
     */
    public void save(Home home) {
        this.connector.connect(x -> StatementProvider.create(StatementType.UPDATE, x)
                .table(this.table)
                .column("owner", DataTypes.UUID, home.owner())
                .column("name", DataTypes.STRING, home.name())
                .column("location", DataTypes.LOCATION, home.location())
                .execute());

        List<Home> homes = this.homes.getOrDefault(home.owner(), new ArrayList<>());
        homes.add(home);
        this.homes.put(home.owner(), homes);
    }

    /**
     * Delete a home from the database
     *
     * @param home The home to delete
     */
    public void delete(Home home) {
        this.connector.connect(x -> StatementProvider.create(StatementType.DELETE, x)
                .table(this.table)
                .column("owner", DataTypes.UUID, home.owner())
                .column("name", DataTypes.STRING, home.name())
                .execute());

        List<Home> homes = this.homes.getOrDefault(home.owner(), new ArrayList<>());
        homes.remove(home);
        this.homes.put(home.owner(), homes);
    }

    /**
     * Unload the repository
     */
    @Override
    public void unload() {
        this.homes.clear();
    }

    /**
     * Get all the homes for a player
     *
     * @param owner The owner of the homes
     *
     * @return The homes
     */
    public List<Home> getHomes(UUID owner) {
        if (owner == null) return new ArrayList<>();

        return this.homes.getOrDefault(owner, new ArrayList<>());
    }

    /**
     * Get all the homes for all players
     */
    public Map<UUID, List<Home>> all() {
        return homes;
    }

}
