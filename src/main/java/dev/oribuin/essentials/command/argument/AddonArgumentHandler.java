package dev.oribuin.essentials.command.argument;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.addon.AddonProvider;
import dev.oribuin.essentials.addon.home.database.HomeRepository;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.api.Addon;
import dev.oribuin.essentials.manager.DataManager;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import org.black_ixx.playerpoints.libs.rosegarden.command.framework.ArgumentParser;
import org.black_ixx.playerpoints.libs.rosegarden.command.framework.RoseCommandArgumentInfo;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AddonArgumentHandler extends ArgumentHandler<Addon> {
    
    public AddonArgumentHandler() {
        super(Addon.class);
    }

    @Override
    public Addon handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        Addon addon = AddonProvider.addon(input);
        if (addon == null) throw new HandledArgumentException("argument-handler-addon");
        return addon;
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
