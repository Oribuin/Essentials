package dev.oribuin.essentials.addon.economy.provider;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.economy.config.EconomyConfig;
import dev.oribuin.essentials.addon.economy.model.Transaction;
import dev.oribuin.essentials.addon.economy.model.UserAccount;
import dev.oribuin.essentials.addon.economy.util.NumberUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Vault adapter, uses our economy system to give plugins access to use Vault API
 * <p>
 * {@link UserAccount} User Balance
 */
@SuppressWarnings("deprecation")
public class VaultEconomyProvider implements Economy {

    private final EssentialsPlugin plugin;

    /**
     * Create a new vault economy provider
     *
     * @param plugin The essentials plugin
     */
    public VaultEconomyProvider(EssentialsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the useraccount for a player based on offlineplayer
     *
     * @param player The player to get the balance from
     *
     * @return The balance if available
     */
    private @NotNull UserAccount account(@NotNull OfflinePlayer player) {
        return AddonProvider.ECONOMY_ADDON.getRepository().getBalance(player.getUniqueId());
    }

    /**
     * Get the useraccount for a player based on their username
     *
     * @param playerName The name to get the balance from
     *
     * @return The balance if available
     */
    private @Nullable UserAccount account(@NotNull String playerName) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(playerName);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) return null;

        return AddonProvider.ECONOMY_ADDON.getRepository().getBalance(offlinePlayer.getUniqueId());
    }

    @Override
    public boolean isEnabled() {
        return this.plugin.isEnabled();
    }

    @Override
    public String getName() {
        return "Essentials Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    /**
     * @return No Fractional Digits
     */
    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double v) {
        return NumberUtil.format(v);
    }

    @Override
    public String currencyNamePlural() {
        return EconomyConfig.get().getCurrency().getPlural();
    }

    @Override
    public String currencyNameSingular() {
        return EconomyConfig.get().getCurrency().getSymbol();
    }

    /**
     * Create a player account for the user
     *
     * @param offlinePlayer The player to create an account for
     *
     * @return true if the account was created
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        if (this.hasAccount(offlinePlayer)) return false;

        // Create a new account for the user
        return AddonProvider.ECONOMY_ADDON.deposit(
                offlinePlayer.getUniqueId(),
                EconomyConfig.get().getStartingBalance(),
                "User Account Created"
        ) != null;
    }

    /**
     * Create a player account for the user
     *
     * @param player The player to create an account for
     *
     * @return true if the account was created
     */
    @Override
    public boolean createPlayerAccount(String player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(player);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) return false;

        return this.createPlayerAccount(offlinePlayer);
    }

    /**
     * Create a player account for the user
     *
     * @param player The player to create an account for
     * @param world  THe world to create the account in (invalid)
     *
     * @return true if the account was created
     */
    @Override
    public boolean createPlayerAccount(String player, String world) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(player);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) return false;

        return this.createPlayerAccount(offlinePlayer);
    }

    /**
     * Create a player account for the user
     *
     * @param offlinePlayer The player to create an account for
     *
     * @return true if the account was created
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String world) {
        return this.createPlayerAccount(offlinePlayer);
    }

    /**
     * Check if a user has a balance account on the server
     *
     * @param offlinePlayer The offline player
     *
     * @return if they have an account or not
     */
    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return offlinePlayer.hasPlayedBefore();
    }

    /**
     * Check if a user has a balance account on the server
     *
     * @param player The offline player
     *
     * @return if they have an account or not
     *
     * @deprecated
     */
    @Deprecated
    @Override
    public boolean hasAccount(String player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(player);
        return offlinePlayer != null && offlinePlayer.hasPlayedBefore();
    }

    /**
     * Check if a user has a balance account on the server
     *
     * @param player The offline player
     * @param world  The world the player is in (invalid)
     *
     * @return if they have an account or not
     *
     * @deprecated
     */
    @Deprecated
    @Override
    public boolean hasAccount(String player, String world) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(player);
        return offlinePlayer != null && offlinePlayer.hasPlayedBefore();
    }

    /**
     * Check if a user has a balance account on the server
     *
     * @param offlinePlayer The offline player
     * @param s             The world they're in (invalid)
     *
     * @return if they have an account or not
     */
    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return this.hasAccount(offlinePlayer);
    }

    /**
     * Get the balance of a player
     *
     * @param player The player
     *
     * @return The balance of the player
     *
     * @deprecated
     */
    @Deprecated
    @Override
    public double getBalance(String player) {
        UserAccount account = this.account(player);
        return account != null ? account.getAmount().doubleValue() : 0.0;
    }

    /**
     * Get the balance of an offline player
     *
     * @param offlinePlayer The offline player
     *
     * @return The balance of the player
     */
    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return this.account(offlinePlayer).getAmount().doubleValue();
    }

    /**
     * Get the balance of a player
     *
     * @param player The player
     * @param world  The world the player is in (invalid)
     *
     * @return The balance of the player
     *
     * @deprecated
     */
    @Deprecated
    @Override
    public double getBalance(String player, String world) {
        UserAccount account = this.account(player);
        return account != null ? account.getAmount().doubleValue() : 0.0;
    }


    /**
     * Get the balance of an offline player
     *
     * @param offlinePlayer The offline player
     * @param s             The world they're in (invalid)
     *
     * @return The balance of the player
     */
    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return this.getBalance(offlinePlayer);
    }

    /**
     * Check if an offline player has more than the required amount of money
     *
     * @param player The player
     * @param amount The amount of money to check
     *
     * @return True if they have more
     *
     * @deprecated
     */
    @Deprecated
    @Override
    public boolean has(String player, double amount) {
        UserAccount account = this.account(player);
        double balance = account != null ? account.getAmount().doubleValue() : 0.0;
        return balance >= amount;
    }

    /**
     * Check if an offline player has more than the required amount of money
     *
     * @param offlinePlayer The offline player
     * @param amount        The amount of money to check
     *
     * @return True if they have more
     */
    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        return this.account(offlinePlayer).getAmount().doubleValue() >= amount;
    }

    /**
     * Check if an offline player has more than the required amount of money
     *
     * @param player The player
     * @param world  The world the player is in (invalid)
     * @param amount The amount of money to check
     *
     * @return True if they have more
     *
     * @deprecated
     */
    @Deprecated
    @Override
    public boolean has(String player, String world, double amount) {
        UserAccount account = this.account(player);
        double balance = account != null ? account.getAmount().doubleValue() : 0.0;
        return balance >= amount;
    }


    /**
     * Check if an offline player has more than the required amount of money
     *
     * @param offlinePlayer The offline player
     * @param world         The world the player is in (invalid)
     * @param amount        The amount of money to check
     *
     * @return True if they have more
     */
    @Override
    public boolean has(OfflinePlayer offlinePlayer, String world, double amount) {
        return this.has(offlinePlayer, amount);
    }

    /**
     * Check if an offline player has more than the required amount of money
     *
     * @param player The offline player
     * @param amount The amount of money to check
     *
     * @return True if they have more
     *
     * @deprecated
     */
    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String player, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(player);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Unknown User"
        );

        return this.withdrawPlayer(offlinePlayer, amount);
    }

    /**
     * Withdraw an amount of money from the player
     *
     * @param offlinePlayer The offline player
     * @param amount        The amount of money to take
     *
     * @return The vault economy response
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        Transaction transaction = AddonProvider.ECONOMY_ADDON.deposit(
                offlinePlayer.getUniqueId(),
                BigDecimal.valueOf(-amount),
                "Vault Economy Provider"
        );

        if (transaction == null) return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Unknown Account"
        );

        return transaction.toResponse(true);
    }

    /**
     * Withdraw an amount of money from the player
     *
     * @param player The offline player
     * @param world  The world the player is in (invalid)
     * @param amount The amount of money to check
     *
     * @return True if they have more
     *
     * @deprecated
     */
    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String player, String world, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(player);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Unknown User"
        );

        return this.withdrawPlayer(offlinePlayer, amount);
    }

    /**
     * Withdraw an amount of money from the player
     *
     * @param offlinePlayer The offline player
     * @param world         The world the player is in (invalid)
     * @param amount        The amount of money to take
     *
     * @return The vault economy response
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
        return this.withdrawPlayer(offlinePlayer, amount);
    }

    /**
     * Deposit an amount of money to the player
     *
     * @param player The offline player
     * @param amount The amount of money to check
     *
     * @return True if they have more
     *
     * @deprecated
     */
    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String player, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(player);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Unknown User"
        );

        return this.depositPlayer(offlinePlayer, amount);
    }

    /**
     * Deposit an amount of money to the player
     *
     * @param offlinePlayer The offline player
     * @param amount        The amount of money to add
     *
     * @return The vault economy response
     */
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        Transaction transaction = AddonProvider.ECONOMY_ADDON.deposit(offlinePlayer.getUniqueId(), BigDecimal.valueOf(amount), "Vault Economy Provider");
        if (transaction == null) return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Unknown Account"
        );

        return transaction.toResponse(true);
    }

    /**
     * Deposit an amount of money to the player
     *
     * @param player The offline player
     * @param world  The world the player is in (invalid)
     * @param amount The amount of money to check
     *
     * @return True if they have more
     *
     * @deprecated
     */
    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String player, String world, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(player);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Unknown User"
        );

        return this.depositPlayer(offlinePlayer, amount);
    }

    /**
     * Deposit an amount of money to the player
     *
     * @param offlinePlayer The offline player
     * @param world         The world the player is in (invalid)
     * @param amount        The amount of money to add
     *
     * @return The vault economy response
     */
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
        return this.depositPlayer(offlinePlayer, amount);
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed economy response
     */
    @Override
    public EconomyResponse createBank(String world, String playerName) {
        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "We do not support bank account"
        );
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed economy response
     */
    @Override
    public EconomyResponse createBank(String world, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "We do not support bank account"
        );
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed economy response
     */
    @Override
    public EconomyResponse deleteBank(String user) {
        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "We do not support bank account"
        );
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed economy response
     */
    @Override
    public EconomyResponse bankBalance(String player) {
        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "We do not support bank account"
        );
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed economy response
     */
    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "We do not support bank account"
        );
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed economy response
     */
    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "We do not support bank account"
        );
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed economy response
     */
    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "We do not support bank account"
        );
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed economy response
     */
    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "We do not support bank account"
        );
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed economy response
     */
    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "We do not support bank account"
        );
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed economy response
     */
    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "We do not support bank account"
        );
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed economy response
     */
    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(
                0,
                0,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED,
                "We do not support bank account"
        );
    }

    /**
     * blah blah blah bank stuff we're not doing
     *
     * @return Failed list of banks that don't exist
     */
    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }


}
