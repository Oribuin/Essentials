package dev.oribuin.essentials.addon.economy.model;

import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.serializer.def.DataTypes;

import java.math.BigDecimal;
import java.util.UUID;

public class UserAccount {

    private final UUID player;
    private BigDecimal amount;
    private long lastUpdated;

    /**
     * Create a new user balance instance within the plugin
     *
     * @param player      The player who owns the money
     * @param amount      The amount the player has
     * @param lastUpdated The last time the money was updated in the database
     */
    public UserAccount(UUID player, BigDecimal amount, long lastUpdated) {
        this.player = player;
        this.amount = amount;
        this.lastUpdated = lastUpdated;
    }

    /**
     * Create a new user balance instance within the plugin
     *
     * @param player The player who owns the money
     */
    public UserAccount(UUID player) {
        this(player, BigDecimal.ZERO, 0);
    }

    /**
     * Create a new user balance instance within the plugin
     *
     * @param player The player who owns the money
     * @param amount The starting balance of the account
     */
    public UserAccount(UUID player, BigDecimal amount) {
        this(player, amount, 0);
    }

    /**
     * Create a new user balance instance within the plugin
     *
     * @param player The player who owns the money
     * @param amount The starting balance of the account
     */
    public UserAccount(UUID player, double amount) {
        this(player, BigDecimal.valueOf(amount), 0);
    }


    /**
     * Construct a new user balance from a result set within the database
     *
     * @param row The row to pull the data from
     *
     * @return The UserBalance if available
     */
    public static UserAccount construct(QueryResult.Row row) {
        if (row == null) return null;

        UUID uuid = DataTypes.UUID.deserialize(row, "user");
        BigDecimal amount = DataTypes.BIG_DECIMAL.deserialize(row, "amount");
        long lastUpdated = DataTypes.LONG.deserialize(row, "last_updated");

        return new UserAccount(uuid, amount, lastUpdated);
    }

    @Override
    public String toString() {
        return "UserBalance{" +
               "player=" + player +
               ", amount=" + amount.toString() +
               ", lastUpdated=" + lastUpdated +
               '}';
    }

    public UUID getPlayer() {
        return player;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
