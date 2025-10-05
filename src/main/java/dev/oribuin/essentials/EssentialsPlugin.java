package dev.oribuin.essentials;

import dev.oribuin.essentials.addon.Addon;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.economy.provider.VaultEconomyProvider;
import dev.oribuin.essentials.addon.home.command.argument.HomeArgumentHandler;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.command.argument.AddonArgumentHandler;
import dev.oribuin.essentials.config.ConfigLoader;
import dev.oribuin.essentials.config.impl.MySQLConfig;
import dev.oribuin.essentials.config.impl.PluginMessages;
import dev.oribuin.essentials.hook.plugin.economy.PointsProvider;
import dev.oribuin.essentials.hook.plugin.economy.VaultProvider;
import dev.oribuin.essentials.manager.DataManager;
import dev.oribuin.essentials.scheduler.EssentialScheduler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.setting.Configurable;
import org.incendo.cloud.setting.ManagerSetting;

import javax.naming.NoPermissionException;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("UnstableApiUsage")
public class EssentialsPlugin extends JavaPlugin {

    private static EssentialsPlugin instance;
    private final EssentialScheduler scheduler = EssentialScheduler.getInstance(this);
    private DataManager dataManager;
    private LegacyPaperCommandManager<CommandSender> commandManager;
    private AnnotationParser<CommandSender> parser;
    private ConfigLoader configLoader;

    public EssentialsPlugin() {
        instance = this;
    }

    /**
     * Load all the basic stuff that needs to be done (mostly just economy)
     */
    @Override
    public void onLoad() {
        instance = this;
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
    public void onEnable() {
        this.configLoader = new ConfigLoader();
        this.configLoader.loadConfig(PluginMessages.class, "messages");
        this.configLoader.loadConfig(MySQLConfig.class, "database.yml");

        this.dataManager = new DataManager(this);

        // Register the command manager
        this.commandManager = LegacyPaperCommandManager.createNative(
                this,
                ExecutionCoordinator.asyncCoordinator()
        );

        Configurable<ManagerSetting> commandSettings = this.commandManager.settings();
        commandSettings.set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
        commandSettings.set(ManagerSetting.OVERRIDE_EXISTING_COMMANDS, true);
//        commandSettings.set(ManagerSetting.LIBERAL_FLAG_PARSING, true);

        this.parser = new AnnotationParser<>(this.commandManager, CommandSender.class);
        this.commandManager.parserRegistry().registerParser(
                ParserDescriptor.of(new AddonArgumentHandler(), Addon.class)
        );

        this.commandManager.parserRegistry().registerParser(
                ParserDescriptor.of(new HomeArgumentHandler(), Home.class)
        );

        if (this.commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.commandManager.registerBrigadier();
        } else if (this.commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.commandManager.registerAsynchronousCompletions();
        }

        this.commandManager.exceptionController()
                .registerHandler(NoPermissionException.class, x -> PluginMessages.getInstance()
                        .getNoPermission().send(x.context().sender())
                )
                .registerHandler(InvalidSyntaxException.class, x -> PluginMessages.getInstance()
                        .getInvalidSyntax().send(x.context().sender(), "syntax", x.exception().correctSyntax())
                )
                .registerHandler(InvalidCommandSenderException.class, x -> PluginMessages.getInstance()
                        .getRequirePlayer().send(x.context().sender(), "sender", x.context().sender().getName())
                );

        AddonProvider.init(); // Register default addons for the plugin
        VaultProvider.get(); // Load the vault provider
        PointsProvider.get();  // Load the points provider
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands); // Update the commands for all online players

    }

    public void reload() {
        new HashMap<>(AddonProvider.addons()).forEach((aClass, addon) -> addon.disable());
        new HashMap<>(AddonProvider.addons()).forEach((aClass, addon) -> addon.initialise());

        // Update the commands for all online players
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    public static PluginMessages getMessages() {
        return EssentialsPlugin.getInstance().getConfigLoader().get(PluginMessages.class);
    }

    @Override
    public void onDisable() {
        new ArrayList<>(AddonProvider.addons().values()).forEach(Addon::unload);

        this.dataManager.disable();
    }

    public static EssentialsPlugin getInstance() {
        return instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public LegacyPaperCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    public AnnotationParser<CommandSender> getParser() {
        return parser;
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public EssentialScheduler getScheduler() {
        return scheduler;
    }
}
