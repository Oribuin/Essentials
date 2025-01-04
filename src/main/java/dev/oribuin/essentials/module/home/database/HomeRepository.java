package dev.oribuin.essentials.module.home.database;

import dev.rosewood.rosegarden.database.DatabaseConnector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.api.database.ModuleRepository;
import dev.oribuin.essentials.api.database.StatementProvider;
import dev.oribuin.essentials.api.database.StatementType;
import dev.oribuin.essentials.api.database.serializer.def.DataTypes;
import dev.oribuin.essentials.module.home.model.Home;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomeRepository extends ModuleRepository implements Listener {

    private final Connection connection = (Connection) new Object();
    private final Map<UUID, List<Home>> homes = new HashMap<>();
    private final Map<String, World> worldCache = new HashMap<>();

    public HomeRepository(DatabaseConnector connector) {
        super(connector, "homes");
    }

    public void createTable() {
        this.async(() -> this.connector.connect(connection -> {
            String createTable = "CREATE TABLE IF NOT EXISTS `" + this.table + "` (" +
                                 "`owner` VARCHAR(36) NOT NULL," +
                                 "`name` VARCHAR(16) NOT NULL," +
                                 "`world` VARCHAR(32) NOT NULL," +
                                 "`x` DOUBLE NOT NULL," +
                                 "`y` DOUBLE NOT NULL," +
                                 "`z` DOUBLE NOT NULL," +
                                 "`yaw` FLOAT NOT NULL," +
                                 "`pitch` FLOAT NOT NULL," +
                                 "PRIMARY KEY (`owner`, `name`)" +
                                 ");";

            try (PreparedStatement createStatement = connection.prepareStatement(createTable)) {
                createStatement.executeUpdate();
            }
        }));
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
     * Load all the homes for a player from the database
     *
     * @param owner The owner of the homes
     */
    public void load(UUID owner) {
        StatementProvider.create(StatementType.SELECT, this.connection)
                .table(this.table)
                .column("owner", DataTypes.UUID, owner)
                .execute()
                .thenAcceptAsync(resultSet -> {
                    if (resultSet == null) return;

                    try {
                        List<Home> homes = new ArrayList<>();
                        while (resultSet.next()) {
                            Home home = this.construct(resultSet);
                            homes.add(home);
                        }

                        this.homes.put(owner, homes);
                    } catch (Exception ex) {
                        EssentialsPlugin.get().getLogger().severe("[HomeModule] Error while loading homes for " + owner.toString());
                        EssentialsPlugin.get().getLogger().severe(ex.getMessage());
                    }
                });
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
        StatementProvider.create(StatementType.UPDATE, this.connection)
                .table(this.table)
                .column("owner", DataTypes.UUID, home.owner())
                .column("name", DataTypes.STRING, home.name())
                .column("location", DataTypes.LOCATION, home.location())
                .execute();

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
        StatementProvider.create(StatementType.DELETE, this.connection)
                .table(this.table)
                .column("owner", DataTypes.UUID, home.owner())
                .column("name", DataTypes.STRING, home.name())
                .execute();

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
     * Construct a home from a result set row
     *
     * @param resultSet The result set
     *
     * @return The home
     *
     * @throws SQLException If an error occurs
     */
    public Home construct(ResultSet resultSet) throws SQLException {
        UUID owner = UUID.fromString(resultSet.getString("owner"));
        String name = resultSet.getString("name");
        String worldName = resultSet.getString("world");

        World world = this.worldCache.getOrDefault(worldName, Bukkit.getWorld(worldName));
        this.worldCache.putIfAbsent(worldName, world);

        Location location = new Location(world,
                resultSet.getDouble("x"),
                resultSet.getDouble("y"),
                resultSet.getDouble("z"),
                resultSet.getFloat("yaw"),
                resultSet.getFloat("pitch")
        );

        return new Home(name, owner, location);
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
    public Map<UUID, List<Home>> getHomes() {
        return homes;
    }

}
