package dev.oribuin.essentials.addon.serverlist.config;

import dev.oribuin.essentials.addon.serverlist.ServerListAddon;
import dev.oribuin.essentials.config.AddonConfig;
import dev.oribuin.essentials.config.TextMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class ServerListConfig implements AddonConfig {

    public static ServerListConfig getInstance() {
        return ServerListAddon.getInstance().getConfigLoader().get(ServerListConfig.class);
    }

    @Comment("Whether the basic addon module is enabled")
    private boolean enabled = true;

    @Comment("The maximum amount of players that can be on the server at once (Display Only)")
    private int maxPlayers = 69;

    @Comment("The lines that will be displayed on the server list")
    private List<Lines> lines = new ArrayList<>(List.of(new Lines()));

    @Comment("The list of server icons that will be displayed. File names loaded from ./icons")
    private List<String> iconImages = List.of("server-icon.png");

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<Lines> getLines() {
        return lines;
    }

    public List<String> getIconImages() {
        return iconImages;
    }

    /**
     * Check if the addon config is enabled
     *
     * @return True if the addon config is enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @ConfigSerializable
    @SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
    public static class Lines {
        private TextMessage firstLine = new TextMessage("<rainbow>Welcome to the server!");
        private TextMessage secondLine = new TextMessage("<white>Powered by hamsters");

        public TextMessage getFirstLine() {
            return firstLine;
        }

        public TextMessage getSecondLine() {
            return secondLine;
        }
    }


}
