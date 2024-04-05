package xyz.oribuin.essentials.module.home.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import xyz.oribuin.essentials.module.home.model.Home;

import java.util.List;

public class HomeArgumentHandler extends ArgumentHandler<Home> {

    public HomeArgumentHandler() {
        super(Home.class);
    }

    @Override
    public Home handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        return null;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        return null;
    }

}
