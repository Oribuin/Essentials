package dev.oribuin.essentials.command.argument;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
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
import org.jetbrains.annotations.NotNull;

public class EnchantArgumentHandler implements ArgumentParser<CommandSender, Enchantment> {

    private static final Registry<@NotNull Enchantment> REGISTRY = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

    @Override
    public @NonNull ArgumentParseResult<@NonNull Enchantment> parse(
            @NonNull CommandContext<@NonNull CommandSender> commandContext,
            @NonNull CommandInput commandInput
    ) {
        String input = commandInput.peekString();
        if (input.isEmpty()) {
            return ArgumentParseResult.failure(new EnchantArgumentHandler.EnchantParserException(input, commandContext));
        }

        Enchantment enchant = REGISTRY.stream()
                .filter(x -> x.key().value().equalsIgnoreCase(input))
                .findFirst()
                .orElse(null);
        
        commandInput.readString();
        
        return enchant != null 
                ? ArgumentParseResult.success(enchant) 
                : ArgumentParseResult.failure(new EnchantArgumentHandler.EnchantParserException(input, commandContext));
    }

    @Override
    public @NonNull SuggestionProvider<CommandSender> suggestionProvider() {
        return SuggestionProvider.blocking((context, input) -> REGISTRY.stream()
                .map(enchantment -> Suggestion.suggestion(enchantment.key().value()))
                .toList());
    }

    public static final class EnchantParserException extends ParserException {

        private final String input;

        public EnchantParserException(String input, CommandContext<?> context) {
            super(EnchantArgumentHandler.class, context, StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, CaptionVariable.of("input", input));

            this.input = input;
        }

        public String getInput() {
            return input;
        }
    }

}