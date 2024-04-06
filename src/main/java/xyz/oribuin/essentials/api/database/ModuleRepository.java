package xyz.oribuin.essentials.api.database;

import dev.rosewood.rosegarden.database.DatabaseConnector;
import org.bukkit.Bukkit;
import xyz.oribuin.essentials.Essentials;

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
    public abstract void createTable();

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
        Bukkit.getScheduler().runTaskAsynchronously(Essentials.get(), runnable);
    }


}
