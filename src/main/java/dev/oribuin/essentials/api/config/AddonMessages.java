package dev.oribuin.essentials.api.config;

import dev.oribuin.essentials.api.config.option.TextMessage;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.SettingSerializer;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Creates a new addon message instance <3
 */
public class AddonMessages {

    private final Path parent;
    private final Map<String, TextMessage> messages;
    private final String name;
    private final String prefix;
    private File file;
    private CommentedFileConfiguration config;

    /**
     * Create a new addon messages storage instance used to read/write message locales
     *
     * @param parent The parent directory for the messages
     * @param name   The name of the file
     */
    public AddonMessages(Path parent, String name) {
        this.parent = parent;
        this.name = name.endsWith(".yml") ? name : name + ".yml";
        this.messages = new HashMap<>();
        this.file = new File(this.parent.toFile(), this.name);
        this.prefix = String.format("<#bc7dff><b>%s</b> <gray>|<white>", StringUtils.capitalize(
                parent.getFileName().toString().toLowerCase()
        ));
    }

    /**
     * Register the addon messages from the plugin config
     */
    @SuppressWarnings("unchecked")
    public void register(Plugin plugin) {
        boolean newFile = false;


        if (!file.exists()) {
            try {
                try (InputStream stream = plugin.getResource(parent.getFileName() + "/" + file.getName())) {
                    if (stream == null) {
                        plugin.getLogger().warning("File [" + file.getName() + "] does not exist in resource path [" + file.getPath() + "]. This file will be empty");
                        file.createNewFile();
                    } else {
                        Files.copy(stream, Paths.get(file.getAbsolutePath()));
                    }
                }

                newFile = true;
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create file [" + file.getPath() + "] due to exception: " + e.getMessage());
            }
        }

        boolean changed = false;
        this.config = CommentedFileConfiguration.loadConfiguration(file);
        Map<String, Object> defaultLocaleStrings = this.getDefault(plugin).getOrDefault(
                parent.getFileName() + "/" + file.getName(),
                new HashMap<>()
        );

        StringPlaceholders placeholders = StringPlaceholders.builder().delimiters("{{", "}}")
                .add("PREFIX", this.prefix)
                .build();

        // Apply prefix to all the messages 
        for (Map.Entry<String, Object> entry : defaultLocaleStrings.entrySet()) {
            if (entry.getValue() instanceof String value) {
                defaultLocaleStrings.put(entry.getKey(), placeholders.apply(value));
            } else if (entry.getValue() instanceof List) {
                List<String> list = (List<String>) entry.getValue();
                list.replaceAll(placeholders::apply);
                defaultLocaleStrings.put(entry.getKey(), list);
            }
        }

        // Write new locale values that are missing
        // If the file is new, also write the comments
        for (String key : defaultLocaleStrings.keySet()) {
            Object value = defaultLocaleStrings.get(key);
            if (newFile && key.startsWith(CommentedFileConfiguration.COMMENT_KEY_PREFIX)) {
                this.config.addPathedComments(key, ((String) value).substring(1));
                changed = true;
            } else if (!this.config.contains(key)) {
                this.config.set(key, value);
                changed = true;
            }
        }

        if (changed) this.config.save(file);

        // Load all the messages from the config
        SettingSerializer<TextMessage> serializer = TextMessage.SERIALIZER;
        this.config.save(file);

        for (String key : this.config.getKeys(false)) {
            Object value = this.config.get(key);

            // Parse it as a regular string if it is one :)
            if (value instanceof String string) {
                TextMessage textMessage = new TextMessage(string);
                textMessage.apply(placeholders);
                this.messages.put(key, textMessage);
                continue;
            }

            // Parse it as a text message if possible
            TextMessage textMessage = serializer.read(this.config, key);
            if (textMessage == null) continue;

            textMessage.apply(placeholders);
            this.messages.put(key, textMessage);
        }
    }

    /**
     * Get the default yml files from the source
     *
     * @param plugin The plugin to get the files from
     *
     * @return The returning default files :3
     */
    private Map<String, Map<String, Object>> getDefault(Plugin plugin) {
        Map<String, Map<String, Object>> result = new HashMap<>();
        String parentName = this.parent.getFileName().toString();

        try {
            URL url = AddonMessages.class.getClassLoader().getResource(parentName);
            if (url == null) return result;

            URLConnection connection = url.openConnection();
            JarURLConnection jarConnection = (JarURLConnection) connection;
            JarFile jarFile = jarConnection.getJarFile();
            String prefix = jarConnection.getEntryName() + "/";

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith(prefix) || entry.isDirectory() || !name.endsWith(".yml")) continue;

                try (
                        InputStream inputStream = jarFile.getInputStream(entry);
                        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)
                ) {
                    CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(reader);
                    result.put(entry.getName().toLowerCase(), config.getValues(true));
                }
            }
        } catch (IOException ex) {
            plugin.getLogger().severe("Failed to load resources from path" + parentName);
        }

        return result;
    }

    /**
     * Get the text message from the addon message config
     *
     * @param key The id of the message
     *
     * @return The message if available
     */
    @NotNull
    public TextMessage from(@NotNull String key) {
        return this.messages.getOrDefault(key, new TextMessage(
                "<red>Message [" + key + "] is not available in the [" + this.parent.getFileName() + " /" + this.file.getName() + "]"
        ));
    }

    public Path parent() {
        return parent;
    }

    public Map<String, TextMessage> messages() {
        return messages;
    }

    public String name() {
        return name;
    }

    public File file() {
        return file;
    }

    public void file(File file) {
        this.file = file;
    }

    public CommentedFileConfiguration config() {
        return config;
    }

    public void config(CommentedFileConfiguration config) {
        this.config = config;
    }
}
