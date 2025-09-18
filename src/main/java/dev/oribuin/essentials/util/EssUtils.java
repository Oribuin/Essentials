package dev.oribuin.essentials.util;

import io.papermc.paper.registry.RegistryAccess;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.NamespacedKey;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EssUtils {

    private static final Pattern DURATION_PATTERN = Pattern.compile("(([1-9][0-9]+|[1-9])[dhms])");

    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    public static RegistryAccess REGISTRY = RegistryAccess.registryAccess();
    public static MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    public static final Component TIMER_FINISHED = kyorify("<#3bf731><bold>" + "|".repeat(20));

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
        return MINI_MESSAGE.deserialize(text)
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
        return MINI_MESSAGE.deserialize(placeholders.apply(text))
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

    /**
     * Create a timer bar message counting down until something is ready
     *
     * @param total   The total length of the timer
     * @param current The progress of the timer
     *
     * @return The resulting message
     */
    public static Component createTimerBar(long total, long current) {
        if (current >= total) {
            return kyorify("<#3bf731><bold>" + "|".repeat(20));
        }

        double percentLeft = (double) current / total;
        int textLength = 20;
        int redLength = (int) (textLength - (textLength * percentLeft));
        String redZone = "<#d62822><bold>" + "|".repeat(redLength) + "<reset>";
        String orangeZone = "<#f58516><bold>" + "|".repeat(textLength - redLength);
        return kyorify(redZone + orangeZone);
    }


    /**
     * Convert a string into a duration
     *
     * @param value The value to parse into a duration
     *
     * @return The duration if available
     */
    public static Duration asDuration(String value) {
        if (value == null) return Duration.ZERO;

        Matcher matcher = DURATION_PATTERN.matcher(value.toLowerCase());
        Duration duration = Duration.ZERO;

        while (matcher.find()) {
            String group = matcher.group();
            String timeUnit = String.valueOf(group.charAt(group.length() - 1));
            int timeValue = Integer.parseInt(group.substring(0, group.length() - 1));
            switch (timeUnit) {
                case "d" -> duration = duration.plusDays(timeValue);
                case "h" -> duration = duration.plusHours(timeValue);
                case "m" -> duration = duration.plusMinutes(timeValue);
                case "s" -> duration = duration.plusSeconds(timeValue);
                default -> {
                    // does nothing :3
                }
            }
        }

        return duration;
    }

    /**
     * Convert a duration value into a String
     *
     * @param duration The value
     *
     * @return The duration in a readable format
     */
    public static String fromDuration(Duration duration) {
        return duration.toString().substring(2);
    }

    public static String replaceLegacy(String message) {
        for (LegacyChatColor color : LegacyChatColor.values()) {
            message = message.replace(
                    String.format("&%s", color.getCode()),
                    String.format("<%s>", color.name())
            );

            message = message.replace(
                    String.format("§%s", color.getCode()),
                    String.format("<%s>", color.name())
            );
        }
        return message;
    }
}
