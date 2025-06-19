package dev.simplix.protocolize.bungee.util;

import net.md_5.bungee.nbt.TypedTag;
import net.md_5.bungee.nbt.type.*;
import net.md_5.bungee.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuerzNbtToJoNbtMapper {

    public static ByteTag convertByteTag(net.querz.nbt.tag.ByteTag in) {
        return new ByteTag(in.asByte());
    }

    public static StringTag convertStringTag(net.querz.nbt.tag.StringTag in) {
        return new StringTag(in.getValue());
    }

    public static IntTag convertIntTag(net.querz.nbt.tag.IntTag in) {
        return new IntTag(in.asInt());
    }

    public static ShortTag convertShortTag(net.querz.nbt.tag.ShortTag in) {
        return new ShortTag(in.asShort());
    }

    public static LongTag convertLongTag(net.querz.nbt.tag.LongTag in) {
        return new LongTag(in.asLong());
    }

    public static FloatTag convertFloatTag(net.querz.nbt.tag.FloatTag in) {
        return new FloatTag(in.asFloat());
    }

    public static DoubleTag convertDoubleTag(net.querz.nbt.tag.DoubleTag in) {
        return new DoubleTag(in.asDouble());
    }

    public static ByteArrayTag convertByteArrayTag(net.querz.nbt.tag.ByteArrayTag in) {
        return new ByteArrayTag(in.getValue());
    }

    public static LongArrayTag convertLongArrayTag(net.querz.nbt.tag.LongArrayTag in) {
        return new LongArrayTag(in.getValue());
    }

    public static IntArrayTag convertIntArrayTag(net.querz.nbt.tag.IntArrayTag in) {
        return new IntArrayTag(in.getValue());
    }

    public static ListTag convertListTag(net.querz.nbt.tag.ListTag<?> in) {
        List<TypedTag> tags = new ArrayList<>();
        for (net.querz.nbt.tag.Tag<?> stag : in) {
            tags.add(convertSpecificTag(stag));
        }
        return new ListTag(tags, convertTagType(in.getTypeClass()));
    }

    private static byte convertTagType(Class<?> clazz) {
        if (clazz.equals(net.querz.nbt.tag.ByteTag.class)) {
            return Tag.BYTE;
        } else if (clazz.equals(net.querz.nbt.tag.StringTag.class)) {
            return Tag.STRING;
        } else if (clazz.equals(net.querz.nbt.tag.IntTag.class)) {
            return Tag.INT;
        } else if (clazz.equals(net.querz.nbt.tag.ShortTag.class)) {
            return Tag.SHORT;
        } else if (clazz.equals(net.querz.nbt.tag.LongTag.class)) {
            return Tag.LONG;
        } else if (clazz.equals(net.querz.nbt.tag.FloatTag.class)) {
            return Tag.FLOAT;
        } else if (clazz.equals(net.querz.nbt.tag.DoubleTag.class)) {
            return Tag.DOUBLE;
        } else if (clazz.equals(net.querz.nbt.tag.ByteArrayTag.class)) {
            return Tag.BYTE_ARRAY;
        } else if (clazz.equals(net.querz.nbt.tag.LongArrayTag.class)) {
            return Tag.LONG_ARRAY;
        } else if (clazz.equals(net.querz.nbt.tag.IntArrayTag.class)) {
            return Tag.INT_ARRAY;
        } else if (clazz.equals(net.querz.nbt.tag.ListTag.class)) {
            return Tag.LIST;
        } else if (clazz.equals(net.querz.nbt.tag.CompoundTag.class)) {
            return Tag.COMPOUND;
        }
        throw new IllegalArgumentException("Unsupported tag type " + clazz.getSimpleName());
    }

    public static TypedTag convertSpecificTag(net.querz.nbt.tag.Tag<?> in) {
        if (in instanceof net.querz.nbt.tag.CompoundTag) {
            return convertCompoundTag((net.querz.nbt.tag.CompoundTag) in);
        } else if (in instanceof net.querz.nbt.tag.ByteTag) {
            return convertByteTag((net.querz.nbt.tag.ByteTag) in);
        } else if (in instanceof net.querz.nbt.tag.StringTag) {
            return convertStringTag((net.querz.nbt.tag.StringTag) in);
        } else if (in instanceof net.querz.nbt.tag.IntTag) {
            return convertIntTag((net.querz.nbt.tag.IntTag) in);
        } else if (in instanceof net.querz.nbt.tag.ShortTag) {
            return convertShortTag((net.querz.nbt.tag.ShortTag) in);
        } else if (in instanceof net.querz.nbt.tag.LongTag) {
            return convertLongTag((net.querz.nbt.tag.LongTag) in);
        } else if (in instanceof net.querz.nbt.tag.FloatTag) {
            return convertFloatTag((net.querz.nbt.tag.FloatTag) in);
        } else if (in instanceof net.querz.nbt.tag.DoubleTag) {
            return convertDoubleTag((net.querz.nbt.tag.DoubleTag) in);
        } else if (in instanceof net.querz.nbt.tag.ByteArrayTag) {
            return convertByteArrayTag((net.querz.nbt.tag.ByteArrayTag) in);
        } else if (in instanceof net.querz.nbt.tag.LongArrayTag) {
            return convertLongArrayTag((net.querz.nbt.tag.LongArrayTag) in);
        } else if (in instanceof net.querz.nbt.tag.IntArrayTag) {
            return convertIntArrayTag((net.querz.nbt.tag.IntArrayTag) in);
        } else if (in instanceof net.querz.nbt.tag.ListTag) {
            return convertListTag((net.querz.nbt.tag.ListTag<?>) in);
        } else {
            throw new IllegalArgumentException("Unsupported tag type " + in.getID());
        }
    }

    public static CompoundTag convertCompoundTag(net.querz.nbt.tag.CompoundTag in) {
        CompoundTag out = new CompoundTag();
        for (Map.Entry<String, net.querz.nbt.tag.Tag<?>> tag : in) {
            out.getValue().put(tag.getKey(), convertSpecificTag(tag.getValue()));
        }
        return out;
    }

}
