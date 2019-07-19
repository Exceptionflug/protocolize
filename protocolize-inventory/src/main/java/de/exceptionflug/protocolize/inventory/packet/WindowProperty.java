package de.exceptionflug.protocolize.inventory.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;
import static de.exceptionflug.protocolize.api.util.ProtocolVersions.MINECRAFT_1_13_2;

public class WindowProperty extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING = Maps.newHashMap();

    static {
        MAPPING.put(MINECRAFT_1_8, 0x31);
        MAPPING.put(MINECRAFT_1_9, 0x15);
        MAPPING.put(MINECRAFT_1_9_1, 0x15);
        MAPPING.put(MINECRAFT_1_9_2, 0x15);
        MAPPING.put(MINECRAFT_1_9_3, 0x15);
        MAPPING.put(MINECRAFT_1_10, 0x15);
        MAPPING.put(MINECRAFT_1_11, 0x15);
        MAPPING.put(MINECRAFT_1_11_1, 0x15);
        MAPPING.put(MINECRAFT_1_12, 0x15);
        MAPPING.put(MINECRAFT_1_12_1, 0x15);
        MAPPING.put(MINECRAFT_1_12_2, 0x15);
        MAPPING.put(MINECRAFT_1_13, 0x16);
        MAPPING.put(MINECRAFT_1_13_1, 0x16);
        MAPPING.put(MINECRAFT_1_13_2, 0x16);
        MAPPING.put(MINECRAFT_1_14, 0x15);
        MAPPING.put(MINECRAFT_1_14_1, 0x15);
        MAPPING.put(MINECRAFT_1_14_2, 0x15);
        MAPPING.put(MINECRAFT_1_14_3, 0x15);
        MAPPING.put(MINECRAFT_1_14_4, 0x15);
    }

    private int windowId;
    private short property;
    private short value;

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        buf.writeByte(windowId);
        buf.writeShort(property);
        buf.writeShort(value);
    }

    @Override
    public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        windowId = buf.readUnsignedByte();
        property = buf.readShort();
        value = buf.readShort();
    }

    public int getWindowId() {
        return windowId;
    }

    public WindowProperty setWindowId(final int windowId) {
        this.windowId = windowId;
        return this;
    }

    public short getProperty() {
        return property;
    }

    public WindowProperty setProperty(final short property) {
        this.property = property;
        return this;
    }

    public short getValue() {
        return value;
    }

    public WindowProperty setValue(final short value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final WindowProperty that = (WindowProperty) o;
        return windowId == that.windowId &&
                property == that.property &&
                value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(windowId, property, value);
    }

    @Override
    public String toString() {
        return "WindowProperty{" +
                "windowId=" + windowId +
                ", property=" + property +
                ", value=" + value +
                '}';
    }
}
