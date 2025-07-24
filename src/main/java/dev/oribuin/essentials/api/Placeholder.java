package dev.oribuin.essentials.api;

import dev.oribuin.essentials.util.Placeholders;
import dev.rosewood.rosegarden.utils.StringPlaceholders;

public interface Placeholder {

    /**
     * The string placeholders for this object
     *
     * @return The compiled string placeholders
     */
    default StringPlaceholders placeholders() {
        return Placeholders.empty();
    }

    /**
     * Append a collection of {@link StringPlaceholders} in compound with the current one
     *
     * @param toAppend The placeholder to append
     *
     * @return A compiled {@link StringPlaceholders}
     */
    default StringPlaceholders placeholders(StringPlaceholders... toAppend) {
        StringPlaceholders.Builder builder = Placeholders.builder();
        builder.addAll(this.placeholders());

        // Append any new StringPlaceholders into the new builder
        if (toAppend != null) {
            for (StringPlaceholders append : toAppend) builder.addAll(append);
        }

        return builder.build();
    }

}
