package dev.oribuin.essentials.addon.chat.database;

import com.google.gson.Gson;
import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.addon.chat.channel.ChatChannel;
import dev.oribuin.essentials.database.AddonRepository;
import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.StatementProvider;
import dev.oribuin.essentials.database.StatementType;
import dev.oribuin.essentials.database.connector.DatabaseConnector;
import dev.oribuin.essentials.database.serializer.def.DataTypes;
import dev.oribuin.essentials.scheduler.task.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChatRepository extends AddonRepository {

    private static final Gson GSON = new Gson();
    private final Map<UUID, ChatSender> users = new ConcurrentHashMap<>();
    private final ScheduledTask saveTask;

    public ChatRepository(DatabaseConnector connector) {
        super(connector, "chat_users");

        StatementProvider.create(StatementType.CREATE_TABLE, this)
                .column("uuid", DataTypes.UUID)
                .column("nickname", DataTypes.STRING)
                .column("lastChannel", DataTypes.STRING)
                .column("openDms", DataTypes.BOOLEAN)
                .column("socialSpy", DataTypes.BOOLEAN)
                .column("ignoredUsers", DataTypes.setType(DataTypes.UUID))
                .column("mutedChannels", DataTypes.setType(DataTypes.STRING))
                .primary("uuid")
                .execute();

        // save all the users 
        this.saveTask = ChatAddon.getInstance().getScheduler().runTaskTimerAsync(() -> this.saveDirty(
                this.users.values()
                        .stream()
                        .filter(ChatSender::isDirty)
                        .toList()
        ), 30, 30, TimeUnit.SECONDS);
    }

    /**
     * Load a user from the chat user database into the cache
     *
     * @param user The user to save
     */
    public CompletableFuture<ChatSender> loadSync(@NotNull UUID user) {
        return StatementProvider.create(StatementType.SELECT, this)
                .column("uuid", DataTypes.UUID, user)
                .execute()
                .thenApply(queryResult -> {
                    if (queryResult == null) return new ChatSender(user);

                    QueryResult.Row first = queryResult.first();
                    ChatSender sender = ChatSender.construct(first);
                    ChatSender result = sender != null ? sender : new ChatSender(user);

                    this.users.put(user, result);
                    return result;
                });
    }

    /**
     * Save all the 'dirty' chat senders into the database
     *
     * @param senders The senders to save
     */
    private void saveDirty(List<ChatSender> senders) {
        // manually because statement provider sucks ass
        this.connector.connect(connection -> {
            String query = "REPLACE INTO " + this.table + " (uuid, nickname, lastChannel, openDms, socialSpy, ignoredUsers, mutedChannels) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (ChatSender sender : senders) {
                    statement.setString(1, sender.getUuid().toString());
                    statement.setString(2, sender.getNickname());
                    statement.setString(3, sender.getChannel().getName());
                    statement.setBoolean(4, sender.isOpenDms());
                    statement.setBoolean(5, sender.isSocialSpy());
                    statement.setString(6, GSON.toJson(new ChatSender.UserSet(sender.getIgnoredUsers())));
                    statement.setString(7, GSON.toJson(new ChatSender.ChannelSet(sender.getMutedChannels()
                            .stream()
                            .map(ChatChannel::getName)
                            .collect(Collectors.toSet()))
                    ));

                    statement.addBatch();

                    sender.setDirty(false);
                }

                statement.executeBatch();
            }
        });
    }


    public @NotNull ChatSender get(@NotNull UUID uuid) {
        return this.users.getOrDefault(uuid, new ChatSender(uuid));
    }

    @Override
    public void unload() {
        this.saveDirty(new ArrayList<>(this.users.values()));
        this.saveTask.cancel();
        this.users.clear();
    }

    public Map<UUID, ChatSender> getUsers() {
        return users;
    }

    public ScheduledTask getSaveTask() {
        return saveTask;
    }
}
