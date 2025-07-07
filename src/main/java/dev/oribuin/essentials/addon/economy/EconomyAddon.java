package dev.oribuin.essentials.addon.economy;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.economy.command.EconomyCommand;
import dev.oribuin.essentials.addon.economy.command.impl.PayCommand;
import dev.oribuin.essentials.addon.economy.database.EconomyRepository;
import dev.oribuin.essentials.addon.economy.database.TransactionRepository;
import dev.oribuin.essentials.addon.economy.model.Transaction;
import dev.oribuin.essentials.addon.economy.util.NumberUtil;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.manager.DataManager;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class EconomyAddon extends Addon {

    private EconomyRepository repository;
    private TransactionRepository transactions;

    /**
     * The name of the addon
     * This will be used for logging and the name of the addon.
     */
    @Override
    public String name() {
        return "economy";
    }

    /**
     * When the addon is finished loading and is ready to be used.
     */
    @Override
    public void enable() {
        this.repository = DataManager.create(EconomyRepository.class);
        //        this.transactions = DataManager.create(TransactionRepository.class);

        if (this.repository == null) {
            this.logger.severe("The EconomyRepository is null, this addon will not work correctly");
            AddonProvider.unload(this);
            return;
        }

        // Register Vault as an economy provider
        RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (provider == null || !provider.getPlugin().getName().equalsIgnoreCase(this.plugin.getName())) {
            this.logger.severe("We could not register ourselves as the Vault Economy Provider, This module will not work correctly");
            AddonProvider.unload(this);
            return;
        }

        NumberUtil.setCachedValues();

        this.repository.refreshBatch(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toList());
    }

    /**
     * Deposit a specific amount of money into a persons account
     *
     * @param target The user to deposit the money into
     * @param amount The amount to deposit (negatives to take away)
     */
    public Transaction deposit(@NotNull UUID target, @NotNull Double amount, @NotNull String source) {
        return this.deposit(target, BigDecimal.valueOf(amount), source);
    }

    /**
     * Deposit a specific amount of money into a persons account
     *
     * @param target The user to deposit the money into
     * @param amount The amount to deposit (negatives to take away)
     * @param source The source that is taking away the money
     *
     * @return The appropriate transaction if available
     */
    @Nullable
    public Transaction deposit(@NotNull UUID target, @NotNull BigDecimal amount, @NotNull String source) {
        return this.repository.offset(target, amount, source);
    }

    /**
     * Get all the commands for the addon
     */
    @Override
    public List<BaseRoseCommand> commands() {
        return List.of(new EconomyCommand(this.plugin), new PayCommand(this.plugin));
    }

    /**
     * Get all the listeners for the addon
     */
    @Override
    public List<Listener> listeners() {
        return List.of(this);
    }

    /**
     * Load all the homes for a player from the database
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            this.repository.refresh(event.getUniqueId());
        }
    }

    public EconomyRepository repository() {
        return repository;
    }
}
