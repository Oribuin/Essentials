package dev.oribuin.essentials.api.database;

import dev.rosewood.rosegarden.database.DatabaseConnector;
import org.bukkit.Bukkit;
import dev.oribuin.essentials.EssentialsPlugin;

public abstract class ModuleRepository {

    protected final DatabaseConnector connector;
    protected String table;

    public ModuleRepository(DatabaseConnector connector, String table) {
        this.connector = connector;
        this.table = "essentials_" + table;
    }

    /**
     * Load the repository
     */
    public abstract void establishTables();

    /**
     * Unload the repository
     */
    public abstract void unload();

    /**
     * Execute a task off the main thread
     *
     * @param runnable The task to execute
     */
    public final void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(EssentialsPlugin.get(), runnable);
    }


}
