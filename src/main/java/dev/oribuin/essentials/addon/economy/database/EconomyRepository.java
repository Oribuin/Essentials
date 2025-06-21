package dev.oribuin.essentials.addon.economy.database;

import com.google.common.cache.CacheBuilder;
import dev.oribuin.essentials.addon.economy.model.UserAccount;
import dev.oribuin.essentials.api.database.ModuleRepository;
import dev.oribuin.essentials.api.database.QueryResult;
import dev.oribuin.essentials.api.database.StatementProvider;
import dev.oribuin.essentials.api.database.StatementType;
import dev.oribuin.essentials.api.database.serializer.def.DataTypes;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyRepository extends ModuleRepository implements Listener {

    private final Map<UUID, UserAccount> userBalances = new ConcurrentHashMap<>();

    /**
     * Create a new economy repository to store all the user accounts
     *
     * @param connector The database connector
     */
    public EconomyRepository(DatabaseConnector connector) {
        super(connector, "accounts");

        // Create the necessary table for the user accounts
        StatementProvider.create(StatementType.CREATE_TABLE, this.connector)
                .table(this.table)
                .column("user", DataTypes.UUID)
                .column("amount", DataTypes.BIG_DECIMAL)
                .column("last_updated", DataTypes.LONG)
                .primary("user")
                .execute();
    }

    /**
     * Get a user's account from the database
     *
     * @param owner The owner to get
     *
     * @return The account retrieved from the cache
     */
    public @NotNull UserAccount getBalance(@NotNull UUID owner) {
        return this.userBalances.getOrDefault(owner, new UserAccount(owner));
    }

    /**
     * Get the users account from their uuid or load it from the database
     *
     * @param owner The owner to load
     *
     * @return The owner to load
     */
    public @NotNull CompletableFuture<UserAccount> getOrLoad(@NotNull UUID owner) {
        UserAccount account = this.userBalances.get(owner);
        if (account == null) return this.loadBalance(owner);
        return CompletableFuture.supplyAsync(() -> account);
    }

    /**
     * Load a user's account from the database
     *
     * @param owner The owner of the account
     *
     * @return The account if available
     */
    public @NotNull CompletableFuture<UserAccount> loadBalance(@NotNull UUID owner) {
        return StatementProvider.create(StatementType.SELECT, this.connector)
                .table(this.table)
                .column("user", DataTypes.UUID, owner)
                .execute()
                .thenApply(queryResult -> {
                    UserAccount account = new UserAccount(owner);

                    if (queryResult != null) {
                        QueryResult.Row row = queryResult.first();
                        if (row != null) account = UserAccount.construct(row);
                    }

                    this.userBalances.put(owner, account);
                    return account;
                });
    }

    /**
     * Save a user's account into the database
     *
     * @param account The account to save
     */
    public void saveAccount(UserAccount account) {
        account.lastUpdated(System.currentTimeMillis());

        StatementProvider.create(StatementType.INSERT, this)
                .column("user", DataTypes.UUID, account.player())
                .column("amount", DataTypes.BIG_DECIMAL, account.amount())
                .column("last_updated", DataTypes.LONG, account.lastUpdated())
                .execute()
                .thenRun(() -> this.userBalances.put(account.player(), account));
    }

}
