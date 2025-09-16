package dev.oribuin.essentials.addon.home.command.argument;

import dev.oribuin.essentials.addon.home.database.HomeRepository;
import dev.oribuin.essentials.addon.home.model.Home;
import dev.oribuin.essentials.manager.DataManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class HomeArgumentHandler implements ArgumentParser<CommandSender, Home> {


    /**
     * Parse the home from the plugin data
     *
     * @param commandContext The command context
     * @param commandInput   The command input
     *
     * @return The home parse result if possible
     */
    @Override
    public @NonNull ArgumentParseResult<@NonNull Home> parse(
            @NonNull CommandContext<@NonNull CommandSender> commandContext,
            @NonNull CommandInput commandInput
    ) {
        String input = commandInput.peekString();
        if (input.isEmpty()) return ArgumentParseResult.failure(new HomeParserException(input, commandContext));

        CommandSender sender = commandContext.sender();
        Player targetArgument = commandContext.getOrDefault("target", null);

        if (!(sender instanceof Player) && targetArgument == null) {
            return ArgumentParseResult.failure(new HomeParserException(input, commandContext));
        }

        Player focus = targetArgument != null ? targetArgument : (Player) sender;
        Home home = DataManager.repository(HomeRepository.class).getHomes(focus.getUniqueId()).stream()
                .filter(x -> x.name().equalsIgnoreCase(input))
                .findFirst()
                .orElse(null);

        if (home == null) return ArgumentParseResult.failure(new HomeParserException(input, commandContext));
        return ArgumentParseResult.success(home);
    }

    @Override
    public @NonNull SuggestionProvider<CommandSender> suggestionProvider() {
        return SuggestionProvider.blocking((context, input) -> {
            CommandSender sender = context.sender();
            Player targetArgument = context.getOrDefault("target", null);

            List<Suggestion> empty = List.of();
            if (!(sender instanceof Player) && targetArgument == null) {
                return empty::iterator;
            }

            Player focus = targetArgument != null ? targetArgument : (Player) sender;
            return DataManager.repository(HomeRepository.class).getHomes(focus.getUniqueId())
                    .stream()
                    .map(home -> Suggestion.suggestion(home.name()))
                    .toList();
        });
    }

    public static final class HomeParserException extends ParserException {

        private final String input;

        public HomeParserException(String input, CommandContext<?> context) {
            super(HomeParserException.class, context, StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, CaptionVariable.of("input", input));
            this.input = input;
        }

        public String getInput() {
            return input;
        }
    }
}
