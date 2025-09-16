package dev.oribuin.essentials.database;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.database.connector.DatabaseConnector;

public abstract class ModuleRepository {

    protected final DatabaseConnector connector;
    protected String table;

    public ModuleRepository(DatabaseConnector connector, String table) {
        this.connector = connector;
        this.table = "essentials_" + table;
    }

    public void unload() {}

    /**
     * Execute a task off the main thread
     *
     * @param runnable The task to execute
     */
    public final void async(Runnable runnable) {
        EssentialsPlugin.getInstance().getScheduler().runTaskAsync(runnable);
    }


}
