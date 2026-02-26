package dev.simplix.protocolize.bungee.util;

import net.md_5.bungee.nbt.TypedTag;
import net.md_5.bungee.nbt.NamedTag;
import net.querz.nbt.tag.*;

import java.util.Map;

public class JoNbtToQuerzNbtMapper {

    public static ByteTag convertByteTag(net.md_5.bungee.nbt.type.ByteTag in) {
        return new ByteTag((byte) in.getValue());
    }

    public static StringTag convertStringTag(net.md_5.bungee.nbt.type.StringTag in) {
        return new StringTag(in.getValue());
    }

    public static IntTag convertIntTag(net.md_5.bungee.nbt.type.IntTag in) {
        return new IntTag(in.getValue());
    }

    public static ShortTag convertShortTag(net.md_5.bungee.nbt.type.ShortTag in) {
        return new ShortTag(in.getValue());
    }

    public static LongTag convertLongTag(net.md_5.bungee.nbt.type.LongTag in) {
        return new LongTag(in.getValue());
    }

    public static FloatTag convertFloatTag(net.md_5.bungee.nbt.type.FloatTag in) {
        return new FloatTag(in.getValue());
    }

    public static DoubleTag convertDoubleTag(net.md_5.bungee.nbt.type.DoubleTag in) {
        return new DoubleTag(in.getValue());
    }

    public static ByteArrayTag convertByteArrayTag(net.md_5.bungee.nbt.type.ByteArrayTag in) {
        return new ByteArrayTag(in.getValue());
    }

    public static LongArrayTag convertLongArrayTag(net.md_5.bungee.nbt.type.LongArrayTag in) {
        return new LongArrayTag(in.getValue());
    }

    public static IntArrayTag convertIntArrayTag(net.md_5.bungee.nbt.type.IntArrayTag in) {
        return new IntArrayTag(in.getValue());
    }

    public static ListTag<?> convertListTag(net.md_5.bungee.nbt.type.ListTag in) {
        ListTag tag = new ListTag<>(convertTagType(in.getListType()));
        for (TypedTag stag : in.getValue()) {
            tag.add(convertSpecificTag(stag));
        }
        return tag;
    }

    private static Class<? extends Tag<?>> convertTagType(int type) {
        switch (type) {
            case ByteTag.ID:
                return ByteTag.class;
            case StringTag.ID:
                return StringTag.class;
            case IntTag.ID:
                return IntTag.class;
            case ShortTag.ID:
                return ShortTag.class;
            case LongTag.ID:
                return LongTag.class;
            case FloatTag.ID:
                return FloatTag.class;
            case DoubleTag.ID:
                return DoubleTag.class;
            case ByteArrayTag.ID:
                return ByteArrayTag.class;
            case LongArrayTag.ID:
                return LongArrayTag.class;
            case IntArrayTag.ID:
                return IntArrayTag.class;
            case ListTag.ID:
                throw new IllegalArgumentException("List inside list is not supported");
            case CompoundTag.ID:
                return CompoundTag.class;
            default:
                throw new IllegalArgumentException("Unsupported tag type " + type);
        }
    }

    public static Tag<?> convertSpecificTag(TypedTag in) {
        if (in instanceof CompoundTag) {
            return convertCompoundTag((net.md_5.bungee.nbt.type.CompoundTag) in);
        } else if (in instanceof net.md_5.bungee.nbt.type.ByteTag) {
            return convertByteTag((net.md_5.bungee.nbt.type.ByteTag) in);
        } else if (in instanceof net.md_5.bungee.nbt.type.StringTag) {
            return convertStringTag((net.md_5.bungee.nbt.type.StringTag) in);
        } else if (in instanceof net.md_5.bungee.nbt.type.IntTag) {
            return convertIntTag((net.md_5.bungee.nbt.type.IntTag) in);
        } else if (in instanceof net.md_5.bungee.nbt.type.ShortTag) {
            return convertShortTag((net.md_5.bungee.nbt.type.ShortTag) in);
        } else if (in instanceof net.md_5.bungee.nbt.type.LongTag) {
            return convertLongTag((net.md_5.bungee.nbt.type.LongTag) in);
        } else if (in instanceof net.md_5.bungee.nbt.type.FloatTag) {
            return convertFloatTag((net.md_5.bungee.nbt.type.FloatTag) in);
        } else if (in instanceof net.md_5.bungee.nbt.type.DoubleTag) {
            return convertDoubleTag((net.md_5.bungee.nbt.type.DoubleTag) in);
        } else if (in instanceof net.md_5.bungee.nbt.type.ByteArrayTag) {
            return convertByteArrayTag((net.md_5.bungee.nbt.type.ByteArrayTag) in);
        } else if (in instanceof net.md_5.bungee.nbt.type.LongArrayTag) {
            return convertLongArrayTag((net.md_5.bungee.nbt.type.LongArrayTag) in);
        } else if (in instanceof net.md_5.bungee.nbt.type.IntArrayTag) {
            return convertIntArrayTag((net.md_5.bungee.nbt.type.IntArrayTag) in);
        } else if (in instanceof net.md_5.bungee.nbt.type.ListTag) {
            return convertListTag((net.md_5.bungee.nbt.type.ListTag) in);
        } else {
            throw new IllegalArgumentException("Unsupported tag type " + in.getClass().getSimpleName());
        }
    }

    public static CompoundTag convertCompoundTag(net.md_5.bungee.nbt.type.CompoundTag in) {
        CompoundTag out = new CompoundTag();
        for (Map.Entry<String, TypedTag> tag : in.getValue().entrySet()) {
            out.put(tag.getKey(), convertSpecificTag(tag.getValue()));
        }
        return out;
    }

}
