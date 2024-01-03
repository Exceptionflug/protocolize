package dev.simplix.protocolize.bungee.util;

import net.querz.nbt.tag.*;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.SpecificTag;

public class JoNbtToQuerzNbtMapper {

    public static ByteTag convertByteTag(se.llbit.nbt.ByteTag in) {
        return new ByteTag((byte) in.value);
    }

    public static StringTag convertStringTag(se.llbit.nbt.StringTag in) {
        return new StringTag(in.value);
    }

    public static IntTag convertIntTag(se.llbit.nbt.IntTag in) {
        return new IntTag(in.value);
    }

    public static ShortTag convertShortTag(se.llbit.nbt.ShortTag in) {
        return new ShortTag(in.value);
    }

    public static LongTag convertLongTag(se.llbit.nbt.LongTag in) {
        return new LongTag(in.value);
    }

    public static FloatTag convertFloatTag(se.llbit.nbt.FloatTag in) {
        return new FloatTag(in.value);
    }

    public static DoubleTag convertDoubleTag(se.llbit.nbt.DoubleTag in) {
        return new DoubleTag(in.value);
    }

    public static ByteArrayTag convertByteArrayTag(se.llbit.nbt.ByteArrayTag in) {
        return new ByteArrayTag(in.value);
    }

    public static LongArrayTag convertLongArrayTag(se.llbit.nbt.LongArrayTag in) {
        return new LongArrayTag(in.value);
    }

    public static IntArrayTag convertIntArrayTag(se.llbit.nbt.IntArrayTag in) {
        return new IntArrayTag(in.value);
    }

    public static ListTag<?> convertListTag(se.llbit.nbt.ListTag in) {
        ListTag tag = new ListTag<>(convertTagType(in.type));
        for (SpecificTag stag : in) {
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

    public static Tag<?> convertSpecificTag(SpecificTag in) {
        if (in.isCompoundTag()) {
            return convertCompoundTag(in.asCompound());
        } else if (in instanceof se.llbit.nbt.ByteTag) {
            return convertByteTag((se.llbit.nbt.ByteTag) in);
        } else if (in instanceof se.llbit.nbt.StringTag) {
            return convertStringTag((se.llbit.nbt.StringTag) in);
        } else if (in instanceof se.llbit.nbt.IntTag) {
            return convertIntTag((se.llbit.nbt.IntTag) in);
        } else if (in instanceof se.llbit.nbt.ShortTag) {
            return convertShortTag((se.llbit.nbt.ShortTag) in);
        } else if (in instanceof se.llbit.nbt.LongTag) {
            return convertLongTag((se.llbit.nbt.LongTag) in);
        } else if (in instanceof se.llbit.nbt.FloatTag) {
            return convertFloatTag((se.llbit.nbt.FloatTag) in);
        } else if (in instanceof se.llbit.nbt.DoubleTag) {
            return convertDoubleTag((se.llbit.nbt.DoubleTag) in);
        } else if (in instanceof se.llbit.nbt.ByteArrayTag) {
            return convertByteArrayTag((se.llbit.nbt.ByteArrayTag) in);
        } else if (in instanceof se.llbit.nbt.LongArrayTag) {
            return convertLongArrayTag((se.llbit.nbt.LongArrayTag) in);
        } else if (in instanceof se.llbit.nbt.IntArrayTag) {
            return convertIntArrayTag((se.llbit.nbt.IntArrayTag) in);
        } else if (in instanceof se.llbit.nbt.ListTag) {
            return convertListTag((se.llbit.nbt.ListTag) in);
        } else {
            throw new IllegalArgumentException("Unsupported tag type " + in.tagType());
        }
    }

    public static CompoundTag convertCompoundTag(se.llbit.nbt.CompoundTag in) {
        CompoundTag out = new CompoundTag();
        for (NamedTag tag : in) {
            if (tag.isCompoundTag()) {
                out.put(tag.name(), convertCompoundTag(tag.asCompound()));
            } else {
                out.put(tag.name(), convertSpecificTag(tag.getTag()));
            }
        }
        return out;
    }

}
