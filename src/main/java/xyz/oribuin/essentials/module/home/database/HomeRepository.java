package xyz.oribuin.essentials.module.home.database;

import dev.rosewood.rosegarden.database.DatabaseConnector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.oribuin.essentials.api.database.ModuleRepository;
import xyz.oribuin.essentials.module.home.model.Home;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomeRepository extends ModuleRepository implements Listener {

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
        this.async(() -> this.connector.connect(connection -> {
            String selectHomes = "SELECT * FROM `" + this.table + "` WHERE `owner` = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectHomes)) {
                statement.setString(1, owner.toString());
                ResultSet resultSet = statement.executeQuery();

                List<Home> homes = new ArrayList<>();
                while (resultSet.next()) {
                    Home home = this.construct(resultSet);
                    homes.add(home);
                }

                this.homes.put(owner, homes);
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
        List<Home> homes = this.homes.getOrDefault(home.owner(), new ArrayList<>());
        homes.add(home);

        this.homes.put(home.owner(), homes);
        this.async(() -> this.connector.connect(connection -> {
            String insertHome = "REPLACE INTO `" + this.table + "` (`owner`, `name`, `world`, `x`, `y`, `z`, `yaw`, `pitch`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertHome)) {
                statement.setString(1, home.owner().toString());
                statement.setString(2, home.name());
                statement.setString(3, home.location().getWorld().getName());
                statement.setDouble(4, home.location().getX());
                statement.setDouble(5, home.location().getY());
                statement.setDouble(6, home.location().getZ());
                statement.setFloat(7, home.location().getYaw());
                statement.setFloat(8, home.location().getPitch());

                statement.executeUpdate();
            }
        }));
    }

    /**
     * Delete a home from the database
     *
     * @param home The home to delete
     */
    public void delete(Home home) {
        List<Home> homes = this.homes.getOrDefault(home.owner(), new ArrayList<>());
        homes.remove(home);

        this.homes.put(home.owner(), homes);
        this.async(() -> this.connector.connect(connection -> {
            String deleteHome = "DELETE FROM `" + this.table + "` WHERE `owner` = ? AND `name` = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteHome)) {
                statement.setString(1, home.owner().toString());
                statement.setString(2, home.name());

                statement.executeUpdate();
            }
        }));
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
     * @return The home
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
