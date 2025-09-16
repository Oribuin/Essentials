package dev.oribuin.essentials.database.serializer.impl.spigot;

import dev.oribuin.essentials.database.serializer.impl.mutated.EnumDataType;
import org.bukkit.Material;

public class MaterialDataType extends EnumDataType<Material> {

    public MaterialDataType() {
        super(Material.class);
    }

}
