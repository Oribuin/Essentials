package xyz.oribuin.essentials;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;

import java.util.List;

public class EssentialsPlugin extends RosePlugin {

    public EssentialsPlugin() {
        super(-1, -1, null, null, null, null);
    }

    @Override
    protected void enable() {

    }

    @Override
    protected void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return null;
    }

}
