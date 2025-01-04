package dev.oribuin.essentials.api.database.serializer.impl.spigot;

import dev.oribuin.essentials.api.database.serializer.impl.mutated.EnumDataType;
import org.bukkit.Material;

public class MaterialDataType extends EnumDataType<Material> {

    public MaterialDataType() {
        super(Material.class);
    }

}
