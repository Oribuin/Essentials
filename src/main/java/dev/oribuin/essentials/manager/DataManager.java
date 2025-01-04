package dev.oribuin.essentials.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.Bukkit;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.api.database.ModuleRepository;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DataManager extends AbstractDataManager {

    private static final Map<Class<? extends ModuleRepository>, ModuleRepository> repositories = new HashMap<>();

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * Create a new repository instance and add it to the map
     * You will need to manually load the repository after creating it
     *
     * @param repository The repository class
     */
    public static <T extends ModuleRepository> T create(Class<T> repository) {
        try {
            DatabaseConnector connector = EssentialsPlugin.get().getManager(DataManager.class).databaseConnector;

            if (connector == null) {
                Bukkit.getLogger().severe("Failed to create repository: " + repository.getSimpleName() + " - DatabaseConnector is null");
                return null;
            }

            T repo = repository.getConstructor(DatabaseConnector.class).newInstance(connector);
            repositories.put(repository, repo);
            return repo;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            Bukkit.getLogger().severe("Failed to create repository: " + repository.getSimpleName() + " - " + e.getMessage());
        }

        return null;
    }

    /**
     * Get a repository from the map
     *
     * @param clazz The class of the repository
     * @param <T>   The repository type
     * @return The repository
     */
    public static <T extends ModuleRepository> T getRepository(Class<T> clazz) {
        return clazz.cast(repositories.get(clazz));
    }

    @Override
    public @NotNull List<Supplier<? extends DataMigration>> getDataMigrations() {
        return new ArrayList<>();
    }

}
