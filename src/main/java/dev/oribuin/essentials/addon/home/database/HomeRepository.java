package dev.oribuin.essentials.addon.home.database;

import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.api.database.ModuleRepository;
import dev.oribuin.essentials.api.database.QueryResult;
import dev.oribuin.essentials.api.database.StatementProvider;
import dev.oribuin.essentials.api.database.StatementType;
import dev.oribuin.essentials.api.database.serializer.def.DataTypes;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomeRepository extends ModuleRepository implements Listener {

    private final Map<UUID, List<Home>> homes = new HashMap<>();

    public HomeRepository(DatabaseConnector connector) {
        super(connector, "homes");

        StatementProvider.create(StatementType.CREATE_TABLE, this.connector)
                .table(this.table)
                .column("owner", DataTypes.UUID)
                .column("name", DataTypes.STRING)
                .column("location", DataTypes.LOCATION, false)
                .primary("owner", "name")
                .execute();
    }

    /**
     * Load all the homes for a player from the database
     *
     * @param owner The owner of the homes
     */
    public void load(@NotNull UUID owner) {
        StatementProvider.create(StatementType.SELECT, this.connector)
                .table(this.table)
                .column("owner", DataTypes.UUID, owner)
                .execute()
                .thenAccept(queryResult -> {
                    if (queryResult == null) return;

                    List<Home> results = new ArrayList<>();
                    for (QueryResult.Row row : queryResult.results()) {
                        Home home = Home.construct(row);
                        if (home != null) results.add(home);
                    }

                    this.homes.put(owner, results);
                });
    }

    /**
     * Remove all the homes for a player from the cache
     *
     * @param owner The owner of the homes
     */
    public void unload(@NotNull UUID owner) {
        this.homes.remove(owner);
    }

    /**
     * Save a home to the database
     *
     * @param home The home to save
     */
    public void save(@NotNull Home home) {
        StatementProvider.create(StatementType.INSERT, this.connector)
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
    public void delete(@NotNull Home home) {
        StatementProvider.create(StatementType.DELETE, this.connector)
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
     * Get all the homes for a player
     *
     * @param owner The owner of the homes
     *
     * @return The homes
     */
    @NotNull
    public List<Home> getHomes(@Nullable UUID owner) {
        if (owner == null) return new ArrayList<>();

        return this.homes.getOrDefault(owner, new ArrayList<>());
    }

    /**
     * Get a home from a player by the name of it
     *
     * @param owner The owner of the home
     * @param name  The name of the home
     *
     * @return The home if it exists
     */
    @Nullable
    public Home getHome(@Nullable UUID owner, @Nullable String name) {
        if (owner == null || name == null) return null;

        List<Home> total = this.homes.getOrDefault(owner, new ArrayList<>());
        return total.stream()
                .filter(x -> x.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Check if a home by a certain name already exists
     *
     * @param owner The owner of the home
     * @param name  The name of the home
     *
     * @return True if exists
     */
    public boolean checkExists(@Nullable UUID owner, @Nullable String name) {
        if (owner == null || name == null) return false;

        return this.homes.getOrDefault(owner, new ArrayList<>())
                .stream()
                .anyMatch(x -> x.name().equalsIgnoreCase(name));
    }

    /**
     * Get all the homes for all players
     */
    public Map<UUID, List<Home>> all() {
        return homes;
    }

}
