package dev.oribuin.essentials.addon.warp.command.argument;

import dev.oribuin.essentials.addon.warp.database.WarpRepository;
import dev.oribuin.essentials.addon.warp.model.Warp;
import dev.oribuin.essentials.manager.DataManager;
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

public class WarpArgumentHandler implements ArgumentParser<CommandSender, Warp> {


    /**
     * Parse the Warp from the plugin data
     *
     * @param commandContext The command context
     * @param commandInput   The command input
     *
     * @return The Warp parse result if possible
     */
    @Override
    public @NonNull ArgumentParseResult<@NonNull Warp> parse(
            @NonNull CommandContext<@NonNull CommandSender> commandContext,
            @NonNull CommandInput commandInput
    ) {
        String input = commandInput.peekString();
        if (input.isEmpty()) return ArgumentParseResult.failure(new WarpParserException(input, commandContext));

        Warp warp = DataManager.repository(WarpRepository.class).getWarp(input);
        commandInput.readString();
        
        return warp != null ? ArgumentParseResult.success(warp) : ArgumentParseResult.failure(new WarpParserException(input, commandContext));
    }

    @Override
    public @NonNull SuggestionProvider<CommandSender> suggestionProvider() {
        return SuggestionProvider.blocking((context, input) ->
                DataManager.repository(WarpRepository.class)
                        .getWarps()
                        .stream()
                        .filter(warp -> {
                            if (context.sender() instanceof Player player) {
                                return warp.canAccess(player);
                            }
                            
                            return true;
                        })
                        .map(warp -> Suggestion.suggestion(warp.name()))
                        .toList());
    }

    public static final class WarpParserException extends ParserException {

        private final String input;

        public WarpParserException(String input, CommandContext<?> context) {
            super(WarpParserException.class, context, StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, CaptionVariable.of("input", input));
            this.input = input;
        }

        public String getInput() {
            return input;
        }
    }
}
