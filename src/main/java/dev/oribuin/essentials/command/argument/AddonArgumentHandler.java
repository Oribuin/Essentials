package dev.oribuin.essentials.command.argument;

import dev.oribuin.essentials.addon.Addon;
import dev.oribuin.essentials.addon.AddonProvider;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
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

@SuppressWarnings("UnstableApiUsage")
public class AddonArgumentHandler implements ArgumentParser<CommandSender, Addon> {

    @Override
    public @NonNull ArgumentParseResult<@NonNull Addon> parse(
            @NonNull CommandContext<@NonNull CommandSender> commandContext,
            @NonNull CommandInput commandInput
    ) {
        String input = commandInput.peekString();
        Addon addon = AddonProvider.addon(input);
        if (input.isEmpty() || addon == null) return ArgumentParseResult.failure(new AddonParserException(input, commandContext));

        return ArgumentParseResult.success(addon);
    }

    @Override
    public @NonNull SuggestionProvider<CommandSender> suggestionProvider() {
        return SuggestionProvider.blocking((context, input) ->
                AddonProvider.addons()
                        .values()
                        .stream()
                        .map(addon -> Suggestion.suggestion(addon.getName()))
                        .toList()
        );
    }

    public static final class AddonParserException extends ParserException {

        private final String input;

        public AddonParserException(String input, CommandContext<?> context) {
            super(AddonArgumentHandler.class, context, StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, CaptionVariable.of("input", input));

            this.input = input;
        }

        public String getInput() {
            return input;
        }
    }

}