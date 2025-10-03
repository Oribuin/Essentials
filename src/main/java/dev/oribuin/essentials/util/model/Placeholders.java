package dev.oribuin.essentials.util.model;

import dev.oribuin.essentials.util.StringPlaceholders;
import dev.oribuin.essentials.util.StringPlaceholders.Builder;
import net.kyori.adventure.text.Component;

import java.util.Objects;

public class Placeholders {

    private static final StringPlaceholders EMPTY = Placeholders.builder().build();

    /**
     * @return the empty StringPlaceholders instance
     */
    public static StringPlaceholders empty() {
        return EMPTY;
    }

    /**
     * @return a new StringPlaceholders builder with delimiters initially set to <>
     */
    public static StringPlaceholders.Builder builder() {
        return StringPlaceholders.builder();
    }

    /**
     * Creates a new builder with delimiters initially set to % and a placeholder added
     *
     * @param placeholder the placeholder to add
     * @param value       the value to replace the placeholder with
     *
     * @return a new StringPlaceholders builder with delimiters initially set to % and a placeholder added
     */
    public static Builder builder(String placeholder, String value) {
        return builder().add(placeholder, value);
    }
    /**
     * Creates a new builder with delimiters initially set to % and a placeholder added
     *
     * @param placeholder the placeholder to add
     * @param value       the value to replace the placeholder with
     *
     * @return a new StringPlaceholders builder with delimiters initially set to % and a placeholder added
     */
    public static Builder builder(String placeholder, Component value) {
        return builder().add(placeholder, value);
    }
    /**
     * Creates a new builder with delimiters initially set to % and a placeholder added
     *
     * @param placeholder the placeholder to add
     * @param value       the value to replace the placeholder with
     *
     * @return a new StringPlaceholders builder with delimiters initially set to % and a placeholder added
     */
    public static Builder builder(String placeholder, Object value) {
        return builder().add(placeholder, Objects.toString(value,  "null"));
    }


    /**
     * Creates a new StringPlaceholders instance with delimiters set to % and one placeholder added
     *
     * @param placeholder the placeholder to add
     * @param value       the value to replace the placeholder with
     *
     * @return a new StringPlaceholders instance with delimiters set to % and one placeholder added
     */
    public static StringPlaceholders of(String placeholder, Object value) {
        return builder(placeholder, value).build();
    }

    /**
     * Creates a new StringPlaceholders instance with delimiters set to % and two placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     *
     * @return a new StringPlaceholders instance with delimiters set to % and two placeholders added
     */
    public static StringPlaceholders of(String placeholder1, Object value1,
                                        String placeholder2, Object value2) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .build();
    }

    /**
     * Creates a new StringPlaceholders instance with delimiters set to % and three placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     *
     * @return a new StringPlaceholders instance with delimiters set to % and three placeholders added
     */
    public static StringPlaceholders of(String placeholder1, Object value1,
                                        String placeholder2, Object value2,
                                        String placeholder3, Object value3) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .add(placeholder3, value3)
                .build();
    }

    /**
     * Creates a new StringPlaceholders instance with delimiters set to % and four placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     * @param placeholder4 the fourth placeholder to add
     * @param value4       the value to replace the fourth placeholder with
     *
     * @return a new StringPlaceholders instance with delimiters set to % and four placeholders added
     */
    public static StringPlaceholders of(String placeholder1, Object value1,
                                        String placeholder2, Object value2,
                                        String placeholder3, Object value3,
                                        String placeholder4, Object value4) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .add(placeholder3, value3)
                .add(placeholder4, value4)
                .build();
    }

    /**
     * Creates a new StringPlaceholders instance with delimiters set to % and five placeholders added
     *
     * @param placeholder1 the first placeholder to add
     * @param value1       the value to replace the first placeholder with
     * @param placeholder2 the second placeholder to add
     * @param value2       the value to replace the second placeholder with
     * @param placeholder3 the third placeholder to add
     * @param value3       the value to replace the third placeholder with
     * @param placeholder4 the fourth placeholder to add
     * @param value4       the value to replace the fourth placeholder with
     * @param placeholder5 the fifth placeholder to add
     * @param value5       the value to replace the fifth placeholder with
     *
     * @return a new StringPlaceholders instance with delimiters set to % and five placeholders added
     */
    public static StringPlaceholders of(String placeholder1, Object value1,
                                        String placeholder2, Object value2,
                                        String placeholder3, Object value3,
                                        String placeholder4, Object value4,
                                        String placeholder5, Object value5) {
        return builder(placeholder1, value1)
                .add(placeholder2, value2)
                .add(placeholder3, value3)
                .add(placeholder4, value4)
                .add(placeholder5, value5)
                .build();
    }

}

