package dev.simplix.protocolize.api.util;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.DecoderException;

import java.util.UUID;

/**
 * This class wraps some helper methods. Based on
 * <a href="https://github.com/VelocityPowered/Velocity/blob/a037aebfa0c9a2532246472a170daaa6eb048c40/proxy/src/main/java/com/velocitypowered/proxy/protocol/ProtocolUtils.java">this</a>
 * velocity class.
 */
public final class ProtocolUtil {

    private static final int DEFAULT_MAX_STRING_SIZE = 65536; // 64KiB
    private static final int[] VARINT_EXACT_BYTE_LENGTHS = new int[33];

    static {
        for (int i = 0; i <= 32; ++i) {
            VARINT_EXACT_BYTE_LENGTHS[i] = (int) Math.ceil((31d - (i - 1)) / 7d);
        }
        VARINT_EXACT_BYTE_LENGTHS[32] = 1; // Special case for the number 0.
    }

    private ProtocolUtil() {
    }

    /**
     * Reads a Minecraft-style VarInt from the specified {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the decoded VarInt
     */
    public static int readVarInt(ByteBuf buf) {
        int read = readVarIntSafely(buf);
        if (read == Integer.MIN_VALUE) {
            throw new DecoderException("Bad VarInt decoded");
        }
        return read;
    }

    /**
     * Reads a Minecraft-style VarInt from the specified {@code buf}. The difference between this
     * method and {@link #readVarInt(ByteBuf)} is that this function returns a sentinel value if the
     * varint is invalid.
     *
     * @param buf the buffer to read from
     * @return the decoded VarInt, or {@code Integer.MIN_VALUE} if the varint is invalid
     */
    public static int readVarIntSafely(ByteBuf buf) {
        int i = 0;
        int maxRead = Math.min(5, buf.readableBytes());
        for (int j = 0; j < maxRead; j++) {
            int k = buf.readByte();
            i |= (k & 0x7F) << j * 7;
            if ((k & 0x80) != 128) {
                return i;
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Returns the exact byte size of {@code value} if it were encoded as a VarInt.
     *
     * @param value the value to encode
     * @return the byte size of {@code value} if encoded as a VarInt
     */
    public static int varIntBytes(int value) {
        return VARINT_EXACT_BYTE_LENGTHS[Integer.numberOfLeadingZeros(value)];
    }

    /**
     * Writes a Minecraft-style VarInt to the specified {@code buf}.
     *
     * @param buf   the buffer to read from
     * @param value the integer to write
     */
    public static void writeVarInt(ByteBuf buf, int value) {
        // Peel the one and two byte count cases explicitly as they are the most common VarInt sizes
        // that the proxy will write, to improve inlining.
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else {
            writeVarIntFull(buf, value);
        }
    }

    private static void writeVarIntFull(ByteBuf buf, int value) {
        // See https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else if ((value & (0xFFFFFFFF << 21)) == 0) {
            int w = (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
            buf.writeMedium(w);
        } else if ((value & (0xFFFFFFFF << 28)) == 0) {
            int w = (value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16)
                | ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21);
            buf.writeInt(w);
        } else {
            int w = (value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
                | ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80);
            buf.writeInt(w);
            buf.writeByte(value >>> 28);
        }
    }

    /**
     * Writes the specified {@code value} as a 21-bit Minecraft VarInt to the specified {@code buf}.
     * The upper 11 bits will be discarded.
     *
     * @param buf   the buffer to read from
     * @param value the integer to write
     */
    public static void write21BitVarInt(ByteBuf buf, int value) {
        // See https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
        int w = (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
        buf.writeMedium(w);
    }

    public static String readString(ByteBuf buf) {
        return readString(buf, DEFAULT_MAX_STRING_SIZE);
    }

    private static String readString(ByteBuf buf, int maxLength) {
        int len = readVarInt(buf);
        if (len > maxLength * 4) {
            throw new CorruptedFrameException("Cannot receive string longer than " + maxLength * 4 + " (got " + len + " bytes)");
        }

        byte[] b = new byte[len];
        buf.readBytes(b);

        String s = new String(b, Charsets.UTF_8);
        if (s.length() > maxLength) {
            throw new CorruptedFrameException("Cannot receive string longer than " + maxLength + " (got " + s.length() + " characters)");
        }

        return s;
    }

    /**
     * Writes the specified {@code str} to the {@code buf} with a VarInt prefix.
     *
     * @param buf the buffer to write to
     * @param str the string to write
     */
    public static void writeString(ByteBuf buf, String str) {
        byte[] b = str.getBytes(Charsets.UTF_8);
        writeVarInt(buf, b.length);
        buf.writeBytes(b);
    }

    public static byte[] readByteArray(ByteBuf buf) {
        return readByteArray(buf, DEFAULT_MAX_STRING_SIZE);
    }

    /**
     * Reads a VarInt length-prefixed byte array from the {@code buf}, making sure to not go over
     * {@code cap} size.
     *
     * @param buf the buffer to read from
     * @param cap the maximum size of the string, in UTF-8 character length
     * @return the byte array
     */
    public static byte[] readByteArray(ByteBuf buf, int cap) {
        int length = readVarInt(buf);
        NettyPreconditions.checkFrame(length >= 0, "Got a negative-length array (%s)", length);
        NettyPreconditions.checkFrame(length <= cap, "Bad array size (got %s, maximum is %s)", length, cap);
        NettyPreconditions.checkFrame(buf.isReadable(length),
            "Trying to read an array that is too long (wanted %s, only have %s)", length,
            buf.readableBytes());
        byte[] array = new byte[length];
        buf.readBytes(array);
        return array;
    }

    public static void writeByteArray(ByteBuf buf, byte[] array) {
        writeVarInt(buf, array.length);
        buf.writeBytes(array);
    }

    /**
     * Reads an VarInt-prefixed array of VarInt integers from the {@code buf}.
     *
     * @param buf the buffer to read from
     * @return an array of integers
     */
    public static int[] readIntegerArray(ByteBuf buf) {
        int len = readVarInt(buf);
        Preconditions.checkArgument(len >= 0, "Got a negative-length integer array (%s)", len);
        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
            array[i] = readVarInt(buf);
        }
        return array;
    }

    /**
     * Reads an UUID from the {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the UUID from the buffer
     */
    public static UUID readUniqueId(ByteBuf buf) {
        long msb = buf.readLong();
        long lsb = buf.readLong();
        return new UUID(msb, lsb);
    }

    public static void writeUniqueId(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    /**
     * Reads an UUID stored as an Integer Array from the {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the UUID from the buffer
     */
    public static UUID readUniqueIdIntArray(ByteBuf buf) {
        long msbHigh = (long) buf.readInt() << 32;
        long msbLow = (long) buf.readInt() & 0xFFFFFFFFL;
        long msb = msbHigh | msbLow;
        long lsbHigh = (long) buf.readInt() << 32;
        long lsbLow = (long) buf.readInt() & 0xFFFFFFFFL;
        long lsb = lsbHigh | lsbLow;
        return new UUID(msb, lsb);
    }

    /**
     * Writes an UUID as an Integer Array to the {@code buf}.
     *
     * @param buf  the buffer to write to
     * @param uuid the UUID to write
     */
    public static void writeUniqueIdIntArray(ByteBuf buf, UUID uuid) {
        buf.writeInt((int) (uuid.getMostSignificantBits() >> 32));
        buf.writeInt((int) uuid.getMostSignificantBits());
        buf.writeInt((int) (uuid.getLeastSignificantBits() >> 32));
        buf.writeInt((int) uuid.getLeastSignificantBits());
    }

}
