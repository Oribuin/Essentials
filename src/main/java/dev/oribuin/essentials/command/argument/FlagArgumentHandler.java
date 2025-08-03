package dev.oribuin.essentials.command.argument;

import dev.oribuin.essentials.util.CommandFlag;
import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;

import java.util.List;

public class FlagArgumentHandler extends ArgumentHandler<CommandFlag> {

    private final List<CommandFlag<?>> available;

    public FlagArgumentHandler(CommandFlag<?>... available) {
        super(CommandFlag.class);
        this.available = List.of(available);
    }

    @Override
    public CommandFlag<?> handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        CommandFlag<?> flag = this.from(input);
        if (flag == null) return null;
        if (flag.handler() != null) {
            if (!inputIterator.hasNext()) {
                System.out.println("no additional argument provided:(");
                return null;
            }

//            String next = inputIterator.next();
            // figure this out :33
        }

        return flag;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        if (this.available.isEmpty()) return List.of("<empty>");

        return this.available.stream()
                .map(x -> "--" + x.name().toLowerCase())
                .toList();
    }

    /**
     * Get a command flag from a string
     *
     * @param name The name of the flag
     *
     * @return The flag if available
     */
    private CommandFlag<?> from(String name) {
        String input = name.toLowerCase().replace("--", "");

        return this.available.stream().filter(x -> x.name().equals(input))
                .findFirst()
                .orElse(null);
    }
}
