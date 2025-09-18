package dev.oribuin.essentials.addon.chat;

import dev.oribuin.essentials.addon.Addon;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.chat.command.IgnoreCommand;
import dev.oribuin.essentials.addon.chat.command.MessageCommand;
import dev.oribuin.essentials.addon.chat.command.NickCommand;
import dev.oribuin.essentials.addon.chat.command.RealNameCommand;
import dev.oribuin.essentials.addon.chat.config.ChatConfig;
import dev.oribuin.essentials.addon.chat.config.ChatMessages;
import dev.oribuin.essentials.addon.chat.database.ChatRepository;
import dev.oribuin.essentials.addon.chat.renderer.ChatListener;
import dev.oribuin.essentials.command.AddonCommand;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.manager.DataManager;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class ChatAddon extends Addon {

    private static ChatAddon instance;
    private ChatRepository repository;

    /**
     * Create a new instance of the addon
     */
    public ChatAddon() {
        super("chat");

        instance = this;
    }

    /**
     * When the addon is finished loading and is ready to be used.
     */
    @Override
    public void enable() {
        this.repository = DataManager.create(ChatRepository.class);

        if (this.repository == null) {
            this.logger.severe("The EconomyRepository is null, this addon will not work correctly");
            AddonProvider.unload(this);
        }
    }

    /**
     * Get all the commands for the addon
     */
    @Override
    public List<AddonCommand> getCommands() {
        return List.of(
                new IgnoreCommand(this),
                new MessageCommand(this),
                new NickCommand(this),
                new RealNameCommand(this)
        );
    }

    /**
     * Get all the listeners for the addon
     */
    @Override
    public List<Listener> getListeners() {
        return List.of(new ChatListener(this));
    }

    /**
     * Get all the configuration files for the addon
     */
    @Override
    public Map<String, Supplier<AddonConfig>> getConfigs() {
        return Map.of(
                "config", ChatConfig::new,
                "messages", ChatMessages::new
        );
    }

    /**
     * Get the audience that can view socialspy messages
     *
     * @param participants The people who are participating in the conversation being spied on
     *
     * @return The participating audience
     */
    public Audience getSocialSpyAudience(UUID... participants) {
        List<Audience> audiences = new ArrayList<>(this.repository.getUsers().values()
                .stream()
                .filter(sender -> {
                    boolean socialSpy = sender.isSocialSpy();
                    boolean permission = sender.getPlayerOptional()
                            .filter(x -> x.hasPermission("essentials.socialspy"))
                            .isPresent();

                    boolean isParticipant = List.of(participants).contains(sender.getUuid());
                    return socialSpy && permission && !isParticipant;
                })
                .toList());

        audiences.add(Bukkit.getConsoleSender());
        return Audience.audience(audiences);

    }

    public ChatRepository getRepository() {
        return repository;
    }

    public static ChatAddon getInstance() {
        return instance;
    }

}
