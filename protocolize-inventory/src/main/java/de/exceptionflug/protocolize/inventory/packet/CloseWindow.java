package de.exceptionflug.protocolize.inventory.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class CloseWindow extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING_CLIENTBOUND = Maps.newHashMap();
    public static final Map<Integer, Integer> MAPPING_SERVERBOUND = Maps.newHashMap();

    static {
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_8, 0x2E);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_9, 0x12);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_9_1, 0x12);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_9_2, 0x12);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_9_3, 0x12);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_10, 0x12);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_11, 0x12);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_11_1, 0x12);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_12, 0x12);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_12_1, 0x12);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_12_2, 0x12);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_13, 0x13);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_13_1, 0x13);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_13_2, 0x13);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_14, 0x13);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_1, 0x13);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_2, 0x13);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_3, 0x13);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_4, 0x13);
        MAPPING_CLIENTBOUND.put(MINECRAFT_1_15_PRE6, 0x14);

        MAPPING_SERVERBOUND.put(MINECRAFT_1_8, 0x0D);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_9, 0x08);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_9_1, 0x08);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_9_2, 0x08);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_9_3, 0x08);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_10, 0x08);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_11, 0x08);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_11_1, 0x08);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_12, 0x09);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_12_1, 0x08);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_12_2, 0x08);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_13, 0x09);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_13_1, 0x09);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_13_2, 0x09);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_14, 0x0A);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_14_1, 0x0A);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_14_2, 0x0A);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_14_3, 0x0A);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_14_4, 0x0A);
        MAPPING_SERVERBOUND.put(MINECRAFT_1_15_PRE6, 0x0A);
    }

    private int windowId;

    public CloseWindow(final int windowId) {
        this.windowId = windowId;
    }

    public CloseWindow() {}

    @Override
    public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        if(protocolVersion >= MINECRAFT_1_12 && direction == Direction.TO_CLIENT)
            windowId = buf.readByte();
        else
            windowId = buf.readUnsignedByte();
    }

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        buf.writeByte(windowId);
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(final int windowId) {
        this.windowId = windowId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CloseWindow that = (CloseWindow) o;
        return windowId == that.windowId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(windowId);
    }

    @Override
    public String toString() {
        return "CloseWindow{" +
                "windowId=" + windowId +
                '}';
    }
}
