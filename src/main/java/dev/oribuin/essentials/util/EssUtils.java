package dev.oribuin.essentials.util;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import io.papermc.paper.registry.RegistryAccess;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.NamespacedKey;

import java.util.concurrent.ThreadLocalRandom;

public class EssUtils {

    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    public static RegistryAccess REGISTRY = RegistryAccess.registryAccess();
    public static LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    public EssUtils() {
        throw new IllegalStateException("FishUtil is a utility class and cannot be instantiated.");
    }

    /**
     * Convert a string to a component
     *
     * @param text The text to convert
     *
     * @return The component
     */
    public static Component kyorify(String text) {
        return LEGACY_SERIALIZER.deserialize(text)
                .decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Convert a string into a component with placeholders applied
     *
     * @param text         The text to convert
     * @param placeholders The placeholders to apply
     *
     * @return The component
     */
    public static Component kyorify(String text, StringPlaceholders placeholders) {
        return LEGACY_SERIALIZER.deserialize(placeholders.apply(text))
                .decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Convert a name into an enum value from a specific enum class
     *
     * @param enumClass The enum class
     * @param name      The name of the enum
     * @param <T>       The enum name
     *
     * @return The enum
     */
    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name, T def) {
        if (name == null)
            return def;

        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        return def;
    }

    /**
     * Create a namespaced key from a string value, defaulting to the minecraft namespace
     *
     * @param value The value
     *
     * @return The namespaced key
     */
    public static NamespacedKey key(String value) {
        if (value == null) return null;
        return value.contains(":") ? NamespacedKey.fromString(value) : NamespacedKey.minecraft(value);
    }

    /**
     * Evaluate a math expression and return the result
     *
     * @param expression The expression to evaluate
     *
     * @return The result of the expression
     */
    public static double evaluate(String expression) {
        return new ExpressionBuilder(expression).build().evaluate();
    }
}
