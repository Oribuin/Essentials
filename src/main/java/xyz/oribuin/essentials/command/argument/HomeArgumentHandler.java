package xyz.oribuin.essentials.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.oribuin.essentials.manager.DataManager;
import xyz.oribuin.essentials.module.home.database.HomeRepository;
import xyz.oribuin.essentials.module.home.model.Home;

import java.util.List;

public class HomeArgumentHandler extends ArgumentHandler<Home> {

    public HomeArgumentHandler() {
        super(Home.class);
    }

    @Override
    public Home handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        OfflinePlayer target = context.get("target");
        if (target == null && context.getSender() instanceof Player player) {
            target = player;
        }

        // Target was not defined and the sender is not a player
        if (target == null) throw new HandledArgumentException("argument-handler-home");

        return DataManager.getRepository(HomeRepository.class).getHomes(target.getUniqueId()).stream()
                .filter(home -> home.name().equalsIgnoreCase(inputIterator.next().toLowerCase()))
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
        List<String> result = DataManager.getRepository(HomeRepository.class)
                .getHomes(target.getUniqueId()).stream()
                .map(Home::name)
                .toList();

        return result.isEmpty() ? List.of("<no homes>") : result;
    }

}
