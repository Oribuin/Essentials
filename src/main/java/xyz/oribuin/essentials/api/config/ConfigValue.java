package xyz.oribuin.essentials.api.config;

import java.util.List;

@SuppressWarnings("unchecked")
public record ConfigValue(Object value) {

    public static ConfigValue EMPTY = new ConfigValue(null);

    /**
     * Get the value of the config option as a boolean
     */
    public boolean asBoolean() {
        return value instanceof Boolean result ? result : false;
    }

    /**
     * Get the value of the config option as a String
     */
    public String asString() {
        return value instanceof String result ? result : null;
    }

    /**
     * Get the value of the config option as an integer
     */
    public int asInt() {
        return value instanceof Integer result ? result : 0;
    }

    /**
     * Get the value of the config option as a double
     */
    public double asDouble() {
        return value instanceof Double result ? result : 0.0;
    }

    /**
     * Get the value of the config option as a long
     */
    public long asLong() {
        return value instanceof Long result ? result : 0L;
    }

    /**
     * Get the list of strings from the config option
     *
     * @return The list of strings
     */
    public List<String> asStringList() {
        if (value == null || !(value instanceof List<?>)) return List.of();

        return (List<String>) value;
    }

    /**
     * Get the value of the config option as a specific type
     *
     * @param type The type of the value
     * @param <T>  The type of the value
     * @return The value of the config option
     */
    public <T> T as(Class<T> type) {
        if (value == null) return null;

        return type.cast(value);
    }

}
