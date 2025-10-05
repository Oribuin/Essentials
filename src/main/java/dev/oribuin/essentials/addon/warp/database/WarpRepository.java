package dev.oribuin.essentials.addon.warp.database;

import dev.oribuin.essentials.addon.warp.model.Warp;
import dev.oribuin.essentials.database.AddonRepository;
import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.StatementProvider;
import dev.oribuin.essentials.database.StatementType;
import dev.oribuin.essentials.database.connector.DatabaseConnector;
import dev.oribuin.essentials.database.serializer.def.DataTypes;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WarpRepository extends AddonRepository implements Listener {

    private final List<Warp> warps = new ArrayList<>();

    public WarpRepository(DatabaseConnector connector) {
        super(connector, "warps");

        StatementProvider.create(StatementType.CREATE_TABLE, this.connector)
                .table(this.table)
                .column("name", DataTypes.STRING)
                .column("location", DataTypes.LOCATION, false)
                .primary("name")
                .execute();
        
        this.warps.clear();
        this.load();
    }

    /**
     * Load all the warps into the plugin 
     */
    public void load() {
        StatementProvider.create(StatementType.SELECT, this.connector)
                .table(this.table)
                .execute()
                .thenAccept(queryResult -> {
                    if (queryResult == null) return;

                    List<Warp> results = new ArrayList<>();
                    for (QueryResult.Row row : queryResult.results()) {
                        Warp warp = Warp.construct(row);
                        if (warp != null) results.add(warp);
                    }

                    this.warps.addAll(results);
                });
    }


    /**
     * Save a warp to the database
     *
     * @param warp The warp to save
     */
    public void save(@NotNull Warp warp) {
        StatementProvider.create(StatementType.INSERT, this.connector)
                .table(this.table)
                .column("name", DataTypes.STRING, warp.name())
                .column("location", DataTypes.LOCATION, warp.location())
                .execute();

        this.warps.add(warp);
    }

    /**
     * Delete a warp from the database
     *
     * @param warp The warp to delete
     */
    public void delete(@NotNull Warp warp) {
        StatementProvider.create(StatementType.DELETE, this.connector)
                .table(this.table)
                .column("name", DataTypes.STRING, warp.name())
                .execute();

        this.warps.removeIf(x -> x.name().equalsIgnoreCase(warp.name()));
    }

    /**
     * Unload the repository
     */
    @Override
    public void unload() {
        this.warps.clear();
    }

    /**
     * Get a warp from the cache
     *
     * @param name The name of the warp
     *
     * @return The warp if available
     */
    @Nullable
    public Warp getWarp(String name) {
        return this.warps.stream()
                .filter(warp -> warp.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Warp> getWarps() {
        return warps;
    }
}
