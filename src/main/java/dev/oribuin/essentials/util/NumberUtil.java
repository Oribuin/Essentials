package dev.oribuin.essentials.util;

public final class NumberUtil {

    /**
     * Convert a Double into a String with maximum 2 decimal places
     *
     * @param value The value
     *
     * @return The double as a String
     */
    public static String rounded(Double value) {
        return rounded(value, 2);
    }

    /**
     * Convert a Double into a String with a specified amount of decimal places
     *
     * @param value The value
     *
     * @return The double as a String
     */
    public static String rounded(Double value, int decimals) {
        if (decimals == 0) return String.valueOf((int) Math.round(value));

        String format = "%." + decimals + "f"; // %.2f
        return String.format(format, value);
    }

}
