package dev.oribuin.essentials.api.database.serializer.def;

import dev.oribuin.essentials.api.database.serializer.impl.spigot.ItemStackArrayDataType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import dev.oribuin.essentials.api.database.serializer.DataType;
import dev.oribuin.essentials.api.database.serializer.impl.BooleanDataType;
import dev.oribuin.essentials.api.database.serializer.impl.DateDataType;
import dev.oribuin.essentials.api.database.serializer.impl.DoubleDataType;
import dev.oribuin.essentials.api.database.serializer.impl.mutated.EnumDataType;
import dev.oribuin.essentials.api.database.serializer.impl.FloatDataType;
import dev.oribuin.essentials.api.database.serializer.impl.IntegerDataType;
import dev.oribuin.essentials.api.database.serializer.impl.spigot.ItemStackDataType;
import dev.oribuin.essentials.api.database.serializer.impl.mutated.ListDataType;
import dev.oribuin.essentials.api.database.serializer.impl.spigot.LocationDataType;
import dev.oribuin.essentials.api.database.serializer.impl.LongDataType;
import dev.oribuin.essentials.api.database.serializer.impl.mutated.MapDataType;
import dev.oribuin.essentials.api.database.serializer.impl.spigot.MaterialDataType;
import dev.oribuin.essentials.api.database.serializer.impl.ShortDataType;
import dev.oribuin.essentials.api.database.serializer.impl.StringDataType;
import dev.oribuin.essentials.api.database.serializer.impl.UUIDDataType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataTypes {

    public static final DataType<Boolean> BOOLEAN = new BooleanDataType();
    public static final DataType<Date> DATE = new DateDataType();
    public static final DataType<Double> DOUBLE = new DoubleDataType();
    public static final DataType<Float> FLOAT = new FloatDataType();
    public static final DataType<Integer> INTEGER = new IntegerDataType();
    public static final DataType<Long> LONG = new LongDataType();
    public static final DataType<Short> SHORT = new ShortDataType();
    public static final DataType<String> STRING = new StringDataType();
    public static final DataType<UUID> UUID = new UUIDDataType();

    // Spigot DataTypes
    public static final DataType<Location> LOCATION = new LocationDataType();
    public static final DataType<Material> MATERIAL = new MaterialDataType();
    public static final DataType<ItemStack> ITEM_STACK = new ItemStackDataType();
    public static final DataType<ItemStack[]> ITEM_STACK_ARRAY = new ItemStackArrayDataType();

    /**
     * @return Create a new EnumDataType instance
     */
    public static <T extends Enum<T>> DataType<T> enumType(Class<T> clazz) {
        return new EnumDataType<>(clazz);
    }

    /**
     * @return Create a new ListDataType instance
     */
    public static <T extends List<T>> DataType<T> listType(Class<T> clazz, List<T> list) {
        return new ListDataType<>(clazz, list);
    }

    /**
     * Create a new MapDataType instance with a key and value type
     *
     * @param keyType   The key type
     * @param valueType The value type
     * @param map       The map to use
     *
     * @return Create a new MapDataType instance
     */
    public static <K, V> DataType<Map<DataType<K>, DataType<V>>> mapType(
            DataType<K> keyType,
            DataType<V> valueType,
            Map<DataType<K>, DataType<V>> map
    ) {
        return new MapDataType<>(keyType, valueType, map);
    }

}
