package dev.oribuin.essentials.addon.economy.database;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.economy.config.EconomyConfig;
import dev.oribuin.essentials.addon.economy.model.Transaction;
import dev.oribuin.essentials.addon.economy.model.UserAccount;
import dev.oribuin.essentials.database.ModuleRepository;
import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.StatementProvider;
import dev.oribuin.essentials.database.StatementType;
import dev.oribuin.essentials.database.connector.DatabaseConnector;
import dev.oribuin.essentials.database.serializer.def.DataTypes;
import dev.oribuin.essentials.scheduler.task.ScheduledTask;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class EconomyRepository extends ModuleRepository implements Listener {

    private final LoadingCache<UUID, UserAccount> accountCache;
    private final Map<UUID, Deque<Transaction>> pendingTransactions;
    private ScheduledTask updateTask;

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

        this.pendingTransactions = new ConcurrentHashMap<>();
        this.accountCache = CacheBuilder.newBuilder()
                .concurrencyLevel(2)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .refreshAfterWrite(EconomyConfig.get().getCacheDuration(), TimeUnit.SECONDS)
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull UserAccount load(@NotNull UUID key) {
                        return EconomyRepository.this.getSync(key);
                    }
                });

        this.updateTask = EssentialsPlugin.getInstance().getScheduler().runTaskTimerAsync(this::update, 1L, 1L, TimeUnit.SECONDS);
    }

    /**
     * Unload the repository
     */
    @Override
    public void unload() {
        if (this.updateTask != null) {
            this.updateTask.cancel();
            this.updateTask = null;
        }

        this.update();
        this.accountCache.invalidateAll();
        this.pendingTransactions.clear();
    }

    /**
     * Push any pending account changes to the database
     */
    private void update() {
        Map<UUID, Deque<Transaction>> processing;
        synchronized (this.pendingTransactions) {
            if (this.pendingTransactions.isEmpty()) return;

            processing = new HashMap<>(this.pendingTransactions);
            this.pendingTransactions.clear();
        }

        this.push(processing);
    }

    /**
     * Push all the pending transactions into the database
     *
     * @param processing The transactions to push
     */
    private void push(Map<UUID, Deque<Transaction>> processing) {
        // do this part manually because i dont feel like editing statementprovider :3
        this.async(() -> this.connector.connect(connection -> {
            String query = "REPLACE INTO " + this.table + " (user, amount, last_updated) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (Map.Entry<UUID, Deque<Transaction>> entry : processing.entrySet()) {
                    for (Transaction transaction : entry.getValue()) {
                        statement.setString(1, entry.getKey().toString());
                        statement.setBigDecimal(2, transaction.getCurrent());
                        statement.setLong(3, System.currentTimeMillis());
                        statement.addBatch();
                    }
                }

                statement.executeBatch();
            }
        }));
    }

    /**
     * Get a user's account from the database
     *
     * @param owner The owner to get
     *
     * @return The account retrieved from the cache
     */
    public @NotNull UserAccount getBalance(@NotNull UUID owner) {
        UserAccount account = new UserAccount(owner, EconomyConfig.get().getStartingBalance());
        try {
            account = this.accountCache.get(owner);
        } catch (ExecutionException ignored) {
        }

        Deque<Transaction> pending = this.pendingTransactions.get(owner);
        if (pending != null) {
            BigDecimal current = account.getAmount();
            for (Transaction transaction : pending) {
                current = current.add(transaction.getChange());
            }

            account.setAmount(current);
        }

        return account;
    }

    /**
     * Get a users current balance synchronously, quite unsafe which is why it's private
     *
     * @param owner The owner of the bank balance
     *
     * @return The user account if available
     */
    private @NotNull UserAccount getSync(@NotNull UUID owner) {
        QueryResult result = StatementProvider.create(StatementType.SELECT, this.connector)
                .table(this.table)
                .column("user", DataTypes.UUID, owner)
                .executeSync();

        UserAccount account = new UserAccount(owner, EconomyConfig.get().getStartingBalance());

        if (result != null) {
            QueryResult.Row row = result.first();
            if (row != null) account = UserAccount.construct(row);
        }

        return account;
    }

    /**
     * Refresh a large list of targets from the database
     *
     * @param targets The targets to refresh
     */
    public void refreshBatch(@NotNull List<UUID> targets) {
        // we do this in regular sql because my ass cannot be bothered!!!
        this.async(() -> this.connector.connect(connection -> {
            String select = "SELECT * FROM " + this.table + " WHERE user = ?";
            for (UUID target : targets) {
                try (PreparedStatement statement = connection.prepareStatement(select)) {
                    statement.setString(1, target.toString());

                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        BigDecimal amount = resultSet.getBigDecimal("amount");
                        long lastUpdate = resultSet.getLong("last_updated");
                        this.accountCache.put(target, new UserAccount(target, amount, lastUpdate));
                    }
                }
            }
        }));

    }

    /**
     * Refresh a users current point balance
     *
     * @param owner The owner of the account
     */
    public void refresh(@NotNull UUID owner) {
        this.async(() -> this.accountCache.put(owner, this.getSync(owner)));
    }

    /**
     * Get all the pending transactions for the user
     *
     * @param owner The owner of the account
     *
     * @return Any pending transactions
     */
    public Deque<Transaction> pending(@NotNull UUID owner) {
        return this.pendingTransactions.computeIfAbsent(owner, x -> new ConcurrentLinkedDeque<>());
    }

    /**
     * Publish a new transaction into the transaction queue
     *
     * @param owner       The owner of the account
     * @param transaction The transaction being made
     */
    public boolean publishChange(@NotNull UUID owner, Transaction transaction) {
        this.pending(owner).add(transaction);
        return true;
    }

    /**
     * Publish a new transaction into the transaction queue
     *
     * @param target The target account
     * @param amount The transaction being made
     *
     * @return true if the value could be set
     */
    public boolean set(@NotNull UUID target, BigDecimal amount, String source) {
        if (amount.doubleValue() < 0) return false;

        UserAccount account = this.getBalance(target);
        BigDecimal current = account.getAmount();
        account.setAmount(amount);

        Transaction transaction = new Transaction(target, source, amount, amount);
        transaction.setBefore(current);
        return this.publishChange(target, transaction);
    }

    /**
     * Publish a new transaction into the transaction queue
     *
     * @param target The target account
     * @param amount The transaction being made
     *
     * @return the transaction being made if possible
     */
    @Nullable
    public Transaction offset(@NotNull UUID target, BigDecimal amount, String source) {
        UserAccount userAccount = this.getBalance(target);
        BigDecimal before = userAccount.getAmount();
        BigDecimal newBalance = userAccount.getAmount().add(amount);
        Transaction transaction = new Transaction(
                target,
                source,
                newBalance,
                amount,
                before,
                System.currentTimeMillis()
        );

        userAccount.setAmount(newBalance);
        return this.publishChange(target, transaction) ? transaction : null;
    }


}
