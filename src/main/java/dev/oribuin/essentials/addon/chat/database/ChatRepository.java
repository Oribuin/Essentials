package dev.oribuin.essentials.addon.chat.database;

import com.google.gson.Gson;
import dev.oribuin.essentials.addon.chat.ChatAddon;
import dev.oribuin.essentials.addon.chat.channel.ChatChannel;
import dev.oribuin.essentials.addon.chat.channel.ChatChannels;
import dev.oribuin.essentials.database.AddonRepository;
import dev.oribuin.essentials.database.QueryResult;
import dev.oribuin.essentials.database.StatementProvider;
import dev.oribuin.essentials.database.StatementType;
import dev.oribuin.essentials.database.connector.DatabaseConnector;
import dev.oribuin.essentials.database.serializer.def.DataTypes;
import dev.oribuin.essentials.scheduler.task.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                .column("channel", DataTypes.STRING)
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

    public CompletableFuture<ChatSender> getAsync(@NotNull UUID uuid) {
        if (this.users.containsKey(uuid)) {
            return CompletableFuture.supplyAsync(() -> this.users.get(uuid));
        }

        return CompletableFuture.supplyAsync(() -> this.load(uuid));
    }

    /**
     * Load a user from the chat user database into the cache
     *
     * @param user The user to save
     */
    public ChatSender load(@NotNull UUID user) {
        ChatSender sender = new ChatSender(user);

        try (Connection connection = this.connector.connect(); PreparedStatement statement = connection.prepareStatement(SELECT_USER)) {
            statement.setString(1, user.toString());

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                sender.setNickname(result.getString("nickname"));
                sender.setChannel(ChatChannels.from(result.getString("channel")));
                sender.setOpenDms(result.getBoolean("openDms"));
                sender.setSocialSpy(result.getBoolean("socialSpy"));

                // fuck you values
                Set<String> ignoredUsers = GSON.fromJson(result.getString("ignoredUsers"), StringSet.class).values();
                Set<String> muted = GSON.fromJson(result.getString("mutedChannels"), StringSet.class).values();

                sender.setIgnoredUsers(ignoredUsers.stream().map(UUID::fromString).collect(Collectors.toSet()));
                sender.setMutedChannels(muted.stream().map(ChatChannels::from).collect(Collectors.toSet()));
            }
        } catch (SQLException ex) {
            ChatAddon.getInstance().getLogger().severe("Failed to load user data for id [" + user + "] due to: " + ex.getMessage());
        }
         
        return sender;
    }

    /**
     * Save a user into the database
     *
     * @param sender The user to save
     */
    public void saveUser(ChatSender sender) {
        this.connector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(REPLACE_USER)) {
                this.setUserStatement(sender, statement);
                statement.executeUpdate();
                sender.setDirty(false);
            }
        });
    }
    /**
     * Save a user into the database
     *
     * @param sender The user to save
     */
    public void saveAsync(ChatSender sender) {
        this.async(() -> this.saveUser(sender));
    }
    

    /**
     * Save all the 'dirty' chat senders into the database
     *
     * @param senders The senders to save
     */
    private void saveDirty(Collection<ChatSender> senders) {
        // manually because statement provider sucks ass
        this.connector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(REPLACE_USER)) {
                for (ChatSender sender : senders) {
                    this.setUserStatement(sender, statement);
                    statement.addBatch();
                    sender.setDirty(false);
                }

                statement.executeBatch();
            }
        });
    }

    /**
     * Set the user data inside a prepared statement
     *
     * @param sender    The person to save
     * @param statement The statement to modify
     *
     * @throws SQLException Any errors that may occur
     */
    private void setUserStatement(ChatSender sender, PreparedStatement statement) throws SQLException {
        statement.setString(1, sender.getUuid().toString());
        statement.setString(2, sender.getNickname());
        statement.setString(3, sender.getChannel().getName());
        statement.setBoolean(4, sender.isOpenDms());
        statement.setBoolean(5, sender.isSocialSpy());
        statement.setString(6, GSON.toJson(new StringSet(sender.getIgnoredUsers()
                .stream()
                .map(UUID::toString)
                .collect(Collectors.toSet()))
        ));
        statement.setString(7, GSON.toJson(new StringSet(sender.getMutedChannels()
                .stream()
                .map(ChatChannel::getName)
                .collect(Collectors.toSet()))
        ));
    }

    public @NotNull ChatSender get(@NotNull UUID uuid) {
        return this.users.getOrDefault(uuid, new ChatSender(uuid));
    }

    @Override
    public void unload() {
        this.saveDirty(this.users.values());
        this.saveTask.cancel();
        this.users.clear();
    }

    public Map<UUID, ChatSender> getUsers() {
        return users;
    }

    public ScheduledTask getSaveTask() {
        return saveTask;
    }

    private final String SELECT_USER = "SELECT * FROM " + this.table + " WHERE uuid = ?";
    private final String REPLACE_USER = "REPLACE INTO " + this.table + " (uuid, nickname, channel, openDms, socialSpy, ignoredUsers, mutedChannels) VALUES (?, ?, ?, ?, ?, ?, ?)";


    private record StringSet(Set<String> values) {}
}
