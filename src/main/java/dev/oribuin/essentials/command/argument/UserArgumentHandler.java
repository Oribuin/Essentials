package dev.oribuin.essentials.command.argument;

import dev.oribuin.essentials.util.Placeholders;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * todo: make it actually a User argument handler that does database requests to get offline player without using spigot shit
 */
public class UserArgumentHandler extends ArgumentHandler<OfflinePlayer> {

    public UserArgumentHandler() {
        super(OfflinePlayer.class);
    }

    @Override
    public OfflinePlayer handle(CommandContext commandContext, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        // todo: figure this bitch out, i dont really want to do database requests sync if i can avoid it
        String input = inputIterator.next();
        Player target = Bukkit.getPlayer(input);
        if (target != null) return target;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(input);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
            throw new HandledArgumentException("argument-handler-player", Placeholders.of("input", input));
        }

        return offlinePlayer;
    }

    @Override
    public List<String> suggest(CommandContext commandContext, Argument argument, String[] strings) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers().stream().toList());

        // remove vanished players if sender is a player
        if (commandContext.getSender() instanceof Player player) {
            players.removeIf(x -> !player.canSee(x));
        }

        return players.stream().map(Player::getName).toList();
    }

}
