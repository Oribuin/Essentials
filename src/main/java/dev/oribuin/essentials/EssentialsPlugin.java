package dev.oribuin.essentials;

import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.economy.provider.VaultEconomyProvider;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.hook.plugin.economy.PointsProvider;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.oribuin.essentials.manager.CommandManager;
import dev.oribuin.essentials.manager.DataManager;
import dev.oribuin.essentials.manager.LocaleManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.scheduler.RoseScheduler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EssentialsPlugin extends RosePlugin {

    private static EssentialsPlugin instance;

    public EssentialsPlugin() {
        super(-1, -1,
                DataManager.class,
                LocaleManager.class,
                CommandManager.class
        );

        instance = this;
    }

    /**
     * Load all the basic stuff that needs to be done (mostly just economy)
     */
    @Override
    public void onLoad() {
        super.onLoad();

        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
            this.getServer().getServicesManager().register(
                    Economy.class,
                    new VaultEconomyProvider(this),
                    this,
                    ServicePriority.Normal
            );
        } catch (ClassNotFoundException ex) {
            // grr
        }
    }

    @Override
    public void enable() {
        AddonProvider.init(); // Register default addons for the plugin
        VaultProvider.get(); // Load the vault provider
        PointsProvider.get();  // Load the points provider
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands); // Update the commands for all online players

    }

    @Override
    public void reload() {
        super.reload();

        AddonProvider.addons().forEach((aClass, addon) -> addon.disable());
        AddonProvider.addons().forEach((aClass, addon) -> addon.load());

        // Update the commands for all online players
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    @Override
    public void disable() {
        new ArrayList<>(AddonProvider.addons().values()).forEach(Addon::unload);
    }

    @Override
    protected @NotNull List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of();
    }

    public static EssentialsPlugin get() {
        return instance;
    }

    public static RoseScheduler scheduler() {
        return RoseScheduler.getInstance(instance);
    }

}
