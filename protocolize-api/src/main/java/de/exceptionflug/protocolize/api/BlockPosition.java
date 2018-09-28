package de.exceptionflug.protocolize.api;

import io.netty.buffer.ByteBuf;

import java.util.Objects;

public class BlockPosition {

    private int x, y, z;

    public BlockPosition(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(final int z) {
        this.z = z;
    }

    public void write(final ByteBuf byteBuf) {
        byteBuf.writeLong(((long)x & 67108863) << 38 | ((long)y & 4095) << 26 | ((long)z & 67108863) << 0);
    }

    public static BlockPosition read(final ByteBuf byteBuf) {
        final long val = byteBuf.readLong();
        final int x = (int) (val >> 38);
        final int y = (int) ((val >> 26) & 0xFFF);
        final int z = (int) (val << 38 >> 38);
        return new BlockPosition(x, y, z);
    }

    @Override
    public String toString() {
        return "BlockPosition{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPosition that = (BlockPosition) o;
        return x == that.x &&
                y == that.y &&
                z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
