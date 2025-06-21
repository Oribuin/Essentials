package dev.oribuin.essentials.api.database;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.rosewood.rosegarden.database.DatabaseConnector;

public abstract class ModuleRepository {

    protected final DatabaseConnector connector;
    protected String table;

    public ModuleRepository(DatabaseConnector connector, String table) {
        this.connector = connector;
        this.table = "essentials_" + table;
    }

    /**
     * Unload the repository
     */
    public void unload() {
        if (this.connector != null) {
            this.connector.closeConnection();
        }
    }

    /**
     * Execute a task off the main thread
     *
     * @param runnable The task to execute
     */
    public final void async(Runnable runnable) {
        EssentialsPlugin.scheduler().runTaskAsync(runnable);
    }


}
