package dev.oribuin.essentials.addon.economy.model;

import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.serializer.def.DataTypes;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private @Nullable UUID user; // The person who made a transaction
    private @NotNull String source; // The source of the transaction (if applicable)
    private @NotNull BigDecimal current; // Their current balance after the transaction
    private @NotNull BigDecimal change; // The amount of money being subtracted/added
    private @NotNull BigDecimal before; // The amount of money they had before it was subtracted
    private long when; // When the transaction was made

    /**
     * Create a new economy transaction for the plugin
     *
     * @param user    The player who made the transaction
     * @param source  The plugin source that made the transaction
     * @param current The current balance of the user (post transaction)
     * @param change  The amount of money that was added/removed
     */
    public Transaction(@Nullable UUID user, @NotNull String source, @NotNull BigDecimal current, @NotNull BigDecimal change) {
        this.user = user;
        this.source = source;
        this.current = current;
        this.change = change;
        this.when = System.currentTimeMillis();

        double difference = Math.abs(this.current.doubleValue() + this.change.doubleValue());
        this.before = BigDecimal.valueOf(this.current.doubleValue() + difference);
    }

    /**
     * Create a new economy transaction for the plugin
     *
     * @param user    The player who made the transaction
     * @param source  The plugin source that made the transaction
     * @param current The current balance of the user (post transaction)
     * @param change  The amount of money that was added/removed
     * @param before  The amount of money they had before the transaction
     * @param when    When the transaction was made
     */
    public Transaction(@Nullable UUID user, @NotNull String source, @NotNull BigDecimal current, @NotNull BigDecimal change, @NotNull BigDecimal before, long when) {
        this.user = user;
        this.source = source;
        this.current = current;
        this.change = change;
        this.before = before;
        this.when = when;
    }

    /**
     * Convert a query row into an {@link Transaction}
     *
     * @param row The row to convert
     *
     * @return The deserialized economy transaction
     */
    public static @NotNull Transaction construct(@NotNull QueryResult.Row row) {
        UUID user = DataTypes.UUID.deserialize(row, "user");
        String source = DataTypes.STRING.deserialize(row, "source");
        BigDecimal current = DataTypes.BIG_DECIMAL.deserialize(row, "current");
        BigDecimal change = DataTypes.BIG_DECIMAL.deserialize(row, "change");
        BigDecimal before = DataTypes.BIG_DECIMAL.deserialize(row, "before");
        Long when = DataTypes.LONG.deserialize(row, "time");
        return new Transaction(user, source, current, change, before, when);
    }

    /**
     * Convert an economy response into an economy transaction
     *
     * @param response The economy response to provide
     * @param target   The user who made the transaction
     *
     * @return The economy transaction
     */
    public static Transaction from(EconomyResponse response, UUID target) {
        return new Transaction(
                target,
                "EconomyResponse Migration",
                BigDecimal.valueOf(response.balance),
                BigDecimal.valueOf(response.amount)
        );
    }

    /**
     * Convert an economy transaction into a basic response
     *
     * @param success If the transaction was successful
     *
     * @return The economy response
     */
    public EconomyResponse toResponse(boolean success) {
        return new EconomyResponse(
                this.change.doubleValue(),
                this.current.doubleValue(),
                success ? ResponseType.SUCCESS : ResponseType.FAILURE,
                null
        );
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "user=" + user +
               ", source='" + source + '\'' +
               ", current=" + current +
               ", change=" + change +
               ", before=" + before +
               ", when=" + when +
               '}';
    }

    public @Nullable UUID getUser() {
        return user;
    }

    public void setUser(@Nullable UUID user) {
        this.user = user;
    }

    public @NotNull String getSource() {
        return source;
    }

    public void setSource(@NotNull String source) {
        this.source = source;
    }

    public @NotNull BigDecimal getCurrent() {
        return current;
    }

    public void setCurrent(@NotNull BigDecimal current) {
        this.current = current;
    }

    public @NotNull BigDecimal getChange() {
        return change;
    }

    public void getChange(@NotNull BigDecimal change) {
        this.change = change;
    }

    public @NotNull BigDecimal getBefore() {
        return before;
    }

    public void setBefore(@NotNull BigDecimal before) {
        this.before = before;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

}
