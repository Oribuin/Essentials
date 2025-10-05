package dev.oribuin.essentials.addon.basic.command;

import dev.oribuin.essentials.addon.basic.BasicAddon;
import dev.oribuin.essentials.command.AddonCommand;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class ItemCommand implements AddonCommand {

    private static final Registry<@NotNull Enchantment> ENCHANT_REGISTRY = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    public static final MiniMessage FORMATTER = MiniMessage.miniMessage();
    public static final Map<String, TagResolver> RESOLVER_MAP = Map.of(
            "essentials.item.color", StandardTags.color(),
            "essentials.item.pride", StandardTags.pride(),
            "essentials.item.rainbow", StandardTags.rainbow(),
            "essentials.item.reset", StandardTags.reset(),
            "essentials.item.bold", TagResolver.resolver(Set.of("bold", "b"), (q, c) -> Tag.styling(TextDecoration.BOLD)),
            "essentials.item.underline", TagResolver.resolver(Set.of("underline", "u"), (q, c) -> Tag.styling(TextDecoration.UNDERLINED)),
            "essentials.item.italic", TagResolver.resolver(Set.of("italic", "i", "em"), (q, c) -> Tag.styling(TextDecoration.ITALIC)),
            "essentials.item.strikethrough", TagResolver.resolver(Set.of("strikethrough", "st"), (q, c) -> Tag.styling(TextDecoration.STRIKETHROUGH)),
            "essentials.item.obfuscated", TagResolver.resolver(Set.of("obfuscated", "obf"), (q, c) -> Tag.styling(TextDecoration.OBFUSCATED))
    );

    private final BasicAddon addon;

    public ItemCommand(BasicAddon addon) {
        this.addon = addon;
    }

    /**
     * Change the display name of an item
     *
     * @param sender The sender who is running the command
     * @param name   The name of the item
     */
    @Command("rename|itemname [name]")
    @Permission("essentials.rename")
    @CommandDescription("Change your current player weather")
    public void rename(Player sender, @Greedy String name) {
        ItemStack itemStack = sender.getInventory().getItemInMainHand();
        if (name == null || name.isEmpty()) {
            itemStack.resetData(DataComponentTypes.CUSTOM_NAME);
        } else {
            itemStack.setData(DataComponentTypes.CUSTOM_NAME, deserialize(sender, name));
        }

        sender.sendMessage(Component.text("Renamed item to: ").append(itemStack.displayName()));
    }

    /**
     * Modify the lore of an item in your hand
     *
     * @param sender The sender who is running the command
     * @param line   The lore line to add
     */
    @Command("lore add <line>")
    @Permission("essentials.lore")
    @CommandDescription("Modify the lore of an item in your hand")
    public void loreAdd(Player sender, @Greedy String line) {
        ItemStack itemStack = sender.getInventory().getItemInMainHand();
        ItemLore.Builder itemLore = ItemLore.lore();
        ItemLore present = itemStack.getData(DataComponentTypes.LORE);
        if (present != null) {
            itemLore.addLines(present.lines());
        }

        itemLore.addLine(deserialize(sender, line));
        itemStack.setData(DataComponentTypes.LORE, itemLore);
        sender.sendMessage("Modified Item Lore");
    }

    /**
     * Modify the lore of an item in your hand
     *
     * @param sender The sender who is running the command
     * @param line   The lore line to add
     */
    @Command("lore set <line> <loreLine>")
    @Permission("essentials.lore")
    @CommandDescription("Modify the lore of an item in your hand")
    public void loreSet(Player sender, int line, @Greedy String loreLine) {
        ItemStack itemStack = sender.getInventory().getItemInMainHand();
        ItemLore present = itemStack.getData(DataComponentTypes.LORE);
        List<Component> lore = new ArrayList<>();
        if (present != null) {
            for (int i = 0; i < present.lines().size(); i++) {
                Component presentLine = present.lines().get(i);
                if (i == line) {
                    lore.add(deserialize(sender, loreLine));
                    continue;
                }

                lore.add(presentLine);
            }
        }

        itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        sender.sendMessage("Modified Item Lore");
    }

    /**
     * Modify the lore of an item in your hand
     *
     * @param sender The sender who is running the command
     * @param line   The lore line to add
     */
    @Command("lore remove <line>")
    @Permission("essentials.lore")
    @CommandDescription("Modify the lore of an item in your hand")
    public void loreRemove(Player sender, int line) {
        ItemStack itemStack = sender.getInventory().getItemInMainHand();
        ItemLore present = itemStack.getData(DataComponentTypes.LORE);
        List<Component> lore = new ArrayList<>();
        if (present != null) {
            for (int i = 0; i < present.lines().size(); i++) {
                Component loreLine = present.lines().get(i);
                if (i == line) continue;

                lore.add(loreLine);
            }
        }

        itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        sender.sendMessage("Modified Item Lore");
    }

    /**
     * Modify the lore of an item in your hand
     *
     * @param sender The sender who is running the command
     */
    @Command("lore clear")
    @Permission("essentials.lore")
    @CommandDescription("Modify the lore of an item in your hand")
    public void loreClear(Player sender) {
        ItemStack itemStack = sender.getInventory().getItemInMainHand();
        itemStack.resetData(DataComponentTypes.LORE);
        sender.sendMessage("Modified Item Lore");
    }

    /**
     * Enchant an item in a player's hand to have a specified enchant
     *
     * @param sender  The sender who is running the command
     * @param enchant The enchantment to apply
     * @param level   The level of the enchantment
     */
    @Command("enchantment|enchant <enchant> <level>")
    @Permission("essentials.enchant")
    @CommandDescription("Enchant an item in a player's hand to have a specified enchant")
    public void enchant(Player sender, @Argument(value = "enchant", suggestions = "enchantments") String enchant, int level) {
        ItemStack itemStack = sender.getInventory().getItemInMainHand();
        Enchantment enchantment = ENCHANT_REGISTRY.stream()
                .filter(x -> x.key().value().equalsIgnoreCase(enchant))
                .findFirst()
                .orElse(null);

        if (enchantment == null) {
            // TODO: 
            sender.sendMessage("invalid message");
            return;
        }

        ItemEnchantments.Builder mutable = ItemEnchantments.itemEnchantments();
        ItemEnchantments present = itemStack.getData(DataComponentTypes.ENCHANTMENTS);
        if (present != null) {
            mutable.addAll(present.enchantments());
        }

        int newLevel = Math.min(Math.max(enchantment.getStartLevel(), level), 255);
        mutable.add(enchantment, newLevel);
        itemStack.setData(DataComponentTypes.ENCHANTMENTS, mutable);
        sender.sendMessage("Enchanted item :)");
    }

    private Component deserialize(Player player, String text) {
        return FORMATTER.deserialize(text, TagResolver.resolver(getAvailable(player)))
                .decoration(TextDecoration.ITALIC, false);
    }

    private static List<TagResolver> getAvailable(Player player) {
        return RESOLVER_MAP.entrySet().stream()
                .filter(x -> player.hasPermission(x.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }

    @Suggestions("enchantments")
    public List<String> enchantSuggest(CommandContext<CommandSender> sender, String input) {
        return ENCHANT_REGISTRY.stream().map(enchantment -> enchantment.key().value()).toList();
    }

}
