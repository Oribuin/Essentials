package dev.oribuin.essentials.addon.home.command.argument;

import dev.oribuin.essentials.addon.home.database.HomeRepository;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.manager.DataManager;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeArgumentHandler extends ArgumentHandler<Home> {

    public HomeArgumentHandler() {
        super(Home.class);
    }

    @Override
    public Home handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        CommandSender sender = context.getSender();
        OfflinePlayer target = context.get("target");
        String input = inputIterator.next();

        // todo: better target system
        if (target == null && sender instanceof Player player) {
            target = player;
        }

        // Target was not defined and the sender is not a player
        if (target == null) throw new HandledArgumentException("argument-handler-home");

        return DataManager.repository(HomeRepository.class).getHomes(target.getUniqueId()).stream()
                .filter(home -> home.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new HandledArgumentException("argument-handler-home"));
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        OfflinePlayer target = context.get("target");
        if (target == null && context.getSender() instanceof Player player) {
            target = player;
        }

        // Target was not defined and the sender is not a player
        if (target == null) return List.of("<no homes>");

        // Get the homes of the target
        List<String> result = DataManager.repository(HomeRepository.class)
                .getHomes(target.getUniqueId()).stream()
                .map(Home::name)
                .toList();

        return result.isEmpty() ? List.of("<no homes>") : result;
    }

}
