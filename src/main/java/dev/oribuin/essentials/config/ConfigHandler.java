package dev.oribuin.essentials.config;

import dev.oribuin.essentials.EssentialsPlugin;
import dev.oribuin.essentials.config.serializer.ComponentSerializer;
import dev.oribuin.essentials.config.serializer.DurationSerializer;
import dev.oribuin.essentials.config.serializer.SoundSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

public class ConfigHandler<T> {

    private final Class<T> addonConfig;
    private final Path parent;
    private final String name;
    private ConfigurationReference<@NotNull CommentedConfigurationNode> base;
    private ValueReference<T, @NotNull CommentedConfigurationNode> config;

    /**
     * Create a new config loader for the plugin
     *
     * @param parent The parent folder (Usually the addon folder)
     * @param name   The name of the config
     */
    public ConfigHandler(Class<T> addonConfig, Path parent, String name) {
        this.addonConfig = addonConfig;
        this.parent = parent;
        this.name = name;

        try {
            this.base = ConfigurationReference.fixed(YamlConfigurationLoader.builder()
                    .defaultOptions(options -> options
                            .serializers(builder -> builder
                                    .register(Component.class, ComponentSerializer.getInstance())
                                    .register(Sound.class, SoundSerializer.getInstance())
                                    .register(Duration.class, DurationSerializer.getInstance())
                                    .build()
                            ).shouldCopyDefaults(true)
                    )
                    .nodeStyle(NodeStyle.BLOCK)
                    .indent(2)
                    .path(parent.resolve(name))
                    .build());
            

            this.config = this.base.referenceTo(addonConfig);
            this.base.save();
        } catch (IOException ex) {
            EssentialsPlugin.getInstance().getLogger().severe("Failed to load configuration [" + parent.getFileName() + "/" + name + "] due to: " + ex.getMessage());
        }
    }

    /**
     * Save the configuration file
     */
    public void save() {
        try {
            this.base.node().set(this.addonConfig, this.addonConfig.cast(this.config.get()));
            this.base.loader().save(this.base.node());
        } catch (ConfigurateException ex) {
            EssentialsPlugin.getInstance().getLogger().severe("Failed to save configuration [" + parent.getFileName() + "/" + name + "] due to: " + ex.getMessage());
        }
    }

    /**
     * Unload the config file
     */
    public void unload() {
        this.base.close();
    }

    public T getConfig() {
        return this.config.get();
    }
}
