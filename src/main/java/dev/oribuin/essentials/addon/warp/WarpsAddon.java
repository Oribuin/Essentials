package dev.oribuin.essentials.addon.warp;

import dev.oribuin.essentials.addon.Addon;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.warp.command.WarpDeleteCommand;
import dev.oribuin.essentials.addon.warp.command.WarpSetCommand;
import dev.oribuin.essentials.addon.warp.command.WarpTPCommand;
import dev.oribuin.essentials.addon.warp.command.argument.WarpArgumentHandler;
import dev.oribuin.essentials.addon.warp.config.WarpConfig;
import dev.oribuin.essentials.addon.warp.config.WarpMessages;
import dev.oribuin.essentials.addon.warp.database.WarpRepository;
import dev.oribuin.essentials.addon.warp.model.Warp;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.manager.DataManager;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class WarpsAddon extends Addon implements AddonCommand {

    private WarpRepository repository;
    private static WarpsAddon instance;

    /**
     * Create a new instance of the addon
     */
    public WarpsAddon() {
        super("warps");

        instance = this;
        this.plugin.getCommandManager().parserRegistry().registerParserSupplier(
                TypeToken.get(Warp.class),
                params -> new WarpArgumentHandler()
        );
    }

    /**
     * When the addon is finished loading and is ready to be used.
     */
    @Override
    public void enable() {
        this.repository = DataManager.create(WarpRepository.class);

        if (this.repository == null) {
            this.logger.severe("The WarpRepository is null, this plugin will not work correctly.");
            AddonProvider.unload(this);
            return;
        }

    }

    /**
     * When the addon is being disabled.
     */
    @Override
    public void disable() {
        if (this.repository != null) {
            this.repository.unload();
        }
    }

    /**
     * Get all the commands for the addon
     */
    @Override
    public List<AddonCommand> getCommands() {
        return List.of(
                this,
                new WarpDeleteCommand(this),
                new WarpSetCommand(this),
                new WarpTPCommand()
        );
    }


    /**
     * Get all the configuration files for the addon
     */
    @Override
    public Map<String, Supplier<AddonConfig>> getConfigs() {
        return Map.of(
                "config", WarpConfig::new,
                "messages", WarpMessages::new
        );
    }

    /**
     * Get all the listeners for the addon
     */
    @Override
    public List<Listener> getListeners() {
        return List.of(this);
    }

    /**
     * Get the repository for the addon
     */
    public WarpRepository repository() {
        return repository;
    }

    public static WarpsAddon getInstance() {
        return instance;
    }

}
