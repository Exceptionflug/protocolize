package dev.simplix.protocolize.velocity.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.kyori.adventure.nbt.ArrayBinaryTag;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.ByteArrayBinaryTag;
import net.kyori.adventure.nbt.ByteBinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.FloatBinaryTag;
import net.kyori.adventure.nbt.IntArrayBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;
import net.kyori.adventure.nbt.LongBinaryTag;
import net.kyori.adventure.nbt.ShortBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.ShortTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

public class AdventureNbtToQuerzNbtMapper {

    private static BinaryTagType querzTagTypeToAdventure(Class<?> type) {
        if (type == ByteArrayTag.class)
            return BinaryTagTypes.BYTE_ARRAY;
        else if (type == ByteTag.class)
            return BinaryTagTypes.BYTE;
        else if (type == CompoundTag.class)
            return BinaryTagTypes.COMPOUND;
        else if (type == DoubleTag.class)
            return BinaryTagTypes.DOUBLE;
        else if (type == FloatTag.class)
            return BinaryTagTypes.FLOAT;
        else if (type == IntArrayTag.class)
            return BinaryTagTypes.INT_ARRAY;
        else if (type == IntTag.class)
            return BinaryTagTypes.INT;
        else if (type == ListTag.class)
            return BinaryTagTypes.LIST;
        else if (type == LongArrayTag.class)
            return BinaryTagTypes.LONG_ARRAY;
        else if (type == LongTag.class)
            return BinaryTagTypes.LONG;
        else if (type == ShortTag.class)
            return BinaryTagTypes.SHORT;
        else if (type == StringTag.class)
            return BinaryTagTypes.STRING;
        else
            return null;
    }

    private static Class<? extends Tag> adventureTagTypeToQuerz(BinaryTagType<? extends BinaryTag> type) {
        if (type == BinaryTagTypes.BYTE_ARRAY) {
            return ByteArrayTag.class;
        } else if (type == BinaryTagTypes.BYTE) {
            return ByteTag.class;
        } else if (type == BinaryTagTypes.COMPOUND) {
            return CompoundTag.class;
        } else if (type == BinaryTagTypes.DOUBLE) {
            return DoubleTag.class;
        } else if (type == BinaryTagTypes.FLOAT) {
            return FloatTag.class;
        } else if (type == BinaryTagTypes.INT_ARRAY) {
            return IntArrayTag.class;
        } else if (type == BinaryTagTypes.INT) {
            return IntTag.class;
        } else if (type == BinaryTagTypes.LIST) {
            return ListTag.class;
        } else if (type == BinaryTagTypes.LONG_ARRAY) {
            return LongArrayTag.class;
        } else if (type == BinaryTagTypes.LONG) {
            return LongTag.class;
        } else if (type == BinaryTagTypes.SHORT) {
            return ShortTag.class;
        } else if (type == BinaryTagTypes.STRING) {
            return StringTag.class;
        }

        return null;
    }

    public static BinaryTag querzToAdventure(Tag<?> tag) {
        if (tag instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag) tag;
            Map<String, BinaryTag> entries = new HashMap<>(compoundTag.size());
            for (Map.Entry<String, Tag<?>> entry : compoundTag.entrySet()) {
                entries.put(entry.getKey(), querzToAdventure(entry.getValue()));
            }
            return CompoundBinaryTag.from(entries);
        } else if (tag instanceof ListTag) {
            ListTag<Tag<?>> list = (ListTag<Tag<?>>) tag;
            List<BinaryTag> items = new ArrayList<>(list.size());
            list.iterator().forEachRemaining((item) -> items.add(querzToAdventure(item)));
            return ListBinaryTag.listBinaryTag(querzTagTypeToAdventure(list.getTypeClass()), items);
        } else if (tag instanceof ArrayBinaryTag) {
            if (tag instanceof ByteArrayTag) {
                return ByteArrayBinaryTag.byteArrayBinaryTag(((ByteArrayTag) tag).getValue());
            } else if (tag instanceof IntArrayTag) {
                return IntArrayBinaryTag.intArrayBinaryTag(((IntArrayTag) tag).getValue());
            } else if (tag instanceof LongArrayTag) {
                return LongArrayBinaryTag.longArrayBinaryTag(((LongArrayTag) tag).getValue());
            }
        } else if (tag instanceof ByteTag) {
            return ByteBinaryTag.byteBinaryTag(((ByteTag) tag).asByte());
        } else if (tag instanceof DoubleTag) {
            return DoubleBinaryTag.doubleBinaryTag(((DoubleTag) tag).asDouble());
        } else if (tag instanceof FloatTag) {
            return FloatBinaryTag.floatBinaryTag(((FloatTag) tag).asFloat());
        } else if (tag instanceof IntTag) {
            return IntBinaryTag.intBinaryTag(((IntTag) tag).asInt());
        } else if (tag instanceof LongTag) {
            return LongBinaryTag.longBinaryTag(((LongTag) tag).asLong());
        } else if (tag instanceof ShortTag) {
            return ShortBinaryTag.shortBinaryTag(((ShortTag) tag).asShort());
        } else if (tag instanceof StringTag) {
            return StringBinaryTag.stringBinaryTag(((StringTag) tag).getValue());
        }

        throw new RuntimeException("Unexpected NBT tag type while converting: " + tag.getClass().getName());
    }

    public static Tag<?> adventureToQuerz(BinaryTag tag) {
        if (tag instanceof CompoundBinaryTag) {
            CompoundTag compound = new CompoundTag();
            ((CompoundBinaryTag) tag).iterator().forEachRemaining((entry) -> {
                compound.put(entry.getKey(), adventureToQuerz(entry.getValue()));
            });
            return compound;
        } else if (tag instanceof ListBinaryTag) {
            ListBinaryTag list = (ListBinaryTag) tag;
            ListTag listTag = new ListTag(adventureTagTypeToQuerz(list.elementType()));

            List<Tag<?>> items = new ArrayList<>(list.size());
            list.iterator().forEachRemaining((item) -> items.add(adventureToQuerz(item)));

            listTag.addAll(items);
            return listTag;
        } else if (tag instanceof ArrayBinaryTag) {
            if (tag instanceof ByteArrayBinaryTag) {
                return new ByteArrayTag(((ByteArrayBinaryTag) tag).value());
            } else if (tag instanceof IntArrayBinaryTag) {
                return new IntArrayTag(((IntArrayBinaryTag) tag).value());
            } else if (tag instanceof LongArrayBinaryTag) {
                return new LongArrayTag(((LongArrayBinaryTag) tag).value());
            }
        } else if (tag instanceof ByteBinaryTag) {
            return new ByteTag(((ByteBinaryTag) tag).value());
        } else if (tag instanceof DoubleBinaryTag) {
            return new DoubleTag(((DoubleBinaryTag) tag).value());
        } else if (tag instanceof FloatBinaryTag) {
            return new FloatTag(((FloatBinaryTag) tag).value());
        } else if (tag instanceof IntBinaryTag) {
            return new IntTag(((IntBinaryTag) tag).value());
        } else if (tag instanceof LongBinaryTag) {
            return new LongTag(((LongBinaryTag) tag).value());
        } else if (tag instanceof ShortBinaryTag) {
            return new ShortTag(((ShortBinaryTag) tag).value());
        } else if (tag instanceof StringBinaryTag) {
            return new StringTag(((StringBinaryTag) tag).value());
        }

        throw new RuntimeException("Unexpected NBT tag type while converting: " + tag.getClass().getName());
    }
}
