package dev.oribuin.essentials.addon.economy.database;

import dev.oribuin.essentials.addon.economy.model.Transaction;
import dev.oribuin.essentials.api.database.ModuleRepository;
import dev.oribuin.essentials.api.database.QueryResult;
import dev.oribuin.essentials.api.database.StatementProvider;
import dev.oribuin.essentials.api.database.StatementType;
import dev.oribuin.essentials.api.database.serializer.def.DataTypes;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class TransactionRepository extends ModuleRepository {

    /**
     * Create a new transaction repository to store all the established transactions
     *
     * @param connector The database connector
     */
    public TransactionRepository(DatabaseConnector connector) {
        super(connector, "transactions");

        StatementProvider.create(StatementType.CREATE_TABLE, this)
                .column("user", DataTypes.UUID, true)
                .column("source", DataTypes.STRING)
                .column("current", DataTypes.BIG_DECIMAL)
                .column("change", DataTypes.BIG_DECIMAL)
                .column("before", DataTypes.BIG_DECIMAL)
                .column("time", DataTypes.LONG)
                .execute();
    }

    /**
     * Save a transaction into the plugin logs
     *
     * @param transaction The transaction to save
     */
    public void save(Transaction transaction) {
        StatementProvider.create(StatementType.INSERT, this)
                .column("user", DataTypes.UUID, transaction.user())
                .column("source", DataTypes.STRING, transaction.source())
                .column("current", DataTypes.BIG_DECIMAL, transaction.current())
                .column("change", DataTypes.BIG_DECIMAL, transaction.change())
                .column("before", DataTypes.BIG_DECIMAL, transaction.before())
                .column("time", DataTypes.LONG, transaction.when())
                .execute();
    }

    /**
     * Load all the transactions in the plugin within a specific time period.
     *
     * @param limit     The maximum amount of results to go through
     * @param condition The transaction predicate
     *
     * @return The time to check
     */
    public CompletableFuture<List<Transaction>> loadTransactions(@Nullable Integer limit, @NotNull Predicate<Transaction> condition) {
        return StatementProvider.create(StatementType.SELECT, this)
                .limit(limit != null ? limit : 0)
                .execute()
                .thenApply(result -> {
                    List<Transaction> results = new ArrayList<>();
                    if (result == null) return results;


                    for (QueryResult.Row row : result.results()) {
                        Transaction transaction = Transaction.construct(row);

                        if (condition.test(transaction)) {
                            results.add(transaction);
                        }
                    }

                    return results;
                });
    }
}
