package dev.oribuin.essentials.util;

import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandFlag<T> {

    private final @NotNull String name;
    private @Nullable ArgumentHandler<T> handler;
    private boolean optional;

    /**
     * Create a new command flag to be filled out
     *
     * @param name     The name of the command flag
     * @param handler  The handler if required
     * @param optional Whether the flag is optional
     */
    public CommandFlag(@NotNull String name, @Nullable ArgumentHandler<T> handler, boolean optional) {
        this.name = name;
        this.handler = handler;
        this.optional = optional;
    }

    /**
     * Create a new command flag to be filled out
     *
     * @param name    The name of the command flag
     * @param handler The handler if required
     */
    public CommandFlag(@NotNull String name, @Nullable ArgumentHandler<T> handler) {
        this(name, handler, true);
    }

    /**
     * Create a new command flag to be filled out
     *
     * @param name     The name of the command flag
     * @param optional Whether the command flag is optional
     */
    public CommandFlag(@NotNull String name, boolean optional) {
        this(name, null, optional);
    }

    /**
     * Create a new command flag to be filled out
     *
     * @param name The name of the command flag
     */
    public CommandFlag(@NotNull String name) {
        this(name, null, true);
    }

    public @NotNull String name() {
        return name;
    }

    public @Nullable ArgumentHandler<T> handler() {
        return handler;
    }

    public CommandFlag<T> handler(@Nullable ArgumentHandler<T> handler) {
        this.handler = handler;
        return this;
    }

    public boolean optional() {
        return optional;
    }

    public CommandFlag<T> optional(boolean optional) {
        this.optional = optional;
        return this;
    }
}
