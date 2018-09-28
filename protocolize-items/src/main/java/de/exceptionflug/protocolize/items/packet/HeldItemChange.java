package de.exceptionflug.protocolize.items.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Map;
import java.util.Objects;

public class HeldItemChange extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING_CLIENTBOUND = Maps.newHashMap();
    public static final Map<Integer, Integer> MAPPING_SERVERBOUND = Maps.newHashMap();

    static {
        MAPPING_CLIENTBOUND.put(47, 0x09);
        MAPPING_CLIENTBOUND.put(107, 0x37);
        MAPPING_CLIENTBOUND.put(108, 0x37);
        MAPPING_CLIENTBOUND.put(109, 0x37);
        MAPPING_CLIENTBOUND.put(110, 0x37);
        MAPPING_CLIENTBOUND.put(210, 0x37);
        MAPPING_CLIENTBOUND.put(315, 0x37);
        MAPPING_CLIENTBOUND.put(316, 0x37);
        MAPPING_CLIENTBOUND.put(335, 0x3A);
        MAPPING_CLIENTBOUND.put(338, 0x3A);
        MAPPING_CLIENTBOUND.put(340, 0x3A);
        MAPPING_CLIENTBOUND.put(393, 0x3D);
        MAPPING_CLIENTBOUND.put(401, 0x3D);

        MAPPING_SERVERBOUND.put(47, 0x09);
        MAPPING_SERVERBOUND.put(107, 0x17);
        MAPPING_SERVERBOUND.put(108, 0x17);
        MAPPING_SERVERBOUND.put(109, 0x17);
        MAPPING_SERVERBOUND.put(110, 0x17);
        MAPPING_SERVERBOUND.put(210, 0x17);
        MAPPING_SERVERBOUND.put(315, 0x17);
        MAPPING_SERVERBOUND.put(316, 0x17);
        MAPPING_SERVERBOUND.put(335, 0x1A);
        MAPPING_SERVERBOUND.put(338, 0x1A);
        MAPPING_SERVERBOUND.put(340, 0x1A);
        MAPPING_SERVERBOUND.put(393, 0x21);
        MAPPING_SERVERBOUND.put(401, 0x21);
    }

    private short newSlot;

    public HeldItemChange(final short newSlot) {
        this.newSlot = newSlot;
    }

    public HeldItemChange() {}

    @Override
    public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        if(direction == Direction.TO_SERVER)
            newSlot = buf.readShort();
        else
            newSlot = buf.readByte();
    }

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        if(direction == Direction.TO_SERVER)
            buf.writeShort(newSlot);
        else
            buf.writeByte(newSlot);
    }

    public short getNewSlot() {
        return newSlot;
    }

    public void setNewSlot(final short newSlot) {
        this.newSlot = newSlot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeldItemChange that = (HeldItemChange) o;
        return newSlot == that.newSlot;
    }

    @Override
    public String toString() {
        return "HeldItemChange{" +
                "newSlot=" + newSlot +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(newSlot);
    }
}
