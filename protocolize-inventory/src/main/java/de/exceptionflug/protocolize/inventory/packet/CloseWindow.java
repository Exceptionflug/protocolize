package de.exceptionflug.protocolize.inventory.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Map;
import java.util.Objects;

public class CloseWindow extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING_CLIENTBOUND = Maps.newHashMap();
    public static final Map<Integer, Integer> MAPPING_SERVERBOUND = Maps.newHashMap();

    static {
        MAPPING_CLIENTBOUND.put(47, 0x2E);
        MAPPING_CLIENTBOUND.put(107, 0x12);
        MAPPING_CLIENTBOUND.put(108, 0x12);
        MAPPING_CLIENTBOUND.put(109, 0x12);
        MAPPING_CLIENTBOUND.put(110, 0x12);
        MAPPING_CLIENTBOUND.put(210, 0x12);
        MAPPING_CLIENTBOUND.put(315, 0x12);
        MAPPING_CLIENTBOUND.put(316, 0x12);
        MAPPING_CLIENTBOUND.put(335, 0x12);
        MAPPING_CLIENTBOUND.put(338, 0x12);
        MAPPING_CLIENTBOUND.put(340, 0x12);
        MAPPING_CLIENTBOUND.put(393, 0x13);
        MAPPING_CLIENTBOUND.put(401, 0x13);

        MAPPING_SERVERBOUND.put(47, 0x0D);
        MAPPING_SERVERBOUND.put(107, 0x08);
        MAPPING_SERVERBOUND.put(108, 0x08);
        MAPPING_SERVERBOUND.put(109, 0x08);
        MAPPING_SERVERBOUND.put(110, 0x08);
        MAPPING_SERVERBOUND.put(210, 0x08);
        MAPPING_SERVERBOUND.put(315, 0x08);
        MAPPING_SERVERBOUND.put(316, 0x08);
        MAPPING_SERVERBOUND.put(335, 0x08);
        MAPPING_SERVERBOUND.put(338, 0x08);
        MAPPING_SERVERBOUND.put(340, 0x08);
        MAPPING_SERVERBOUND.put(393, 0x09);
        MAPPING_SERVERBOUND.put(401, 0x09);
    }

    private int windowId;

    public CloseWindow(final int windowId) {
        this.windowId = windowId;
    }

    public CloseWindow() {}

    @Override
    public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        windowId = buf.readUnsignedByte();
    }

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        buf.writeByte(windowId & 0xFF);
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
