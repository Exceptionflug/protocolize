package de.exceptionflug.protocolize.items.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.api.Hand;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Map;
import java.util.Objects;

public class UseItem extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING = Maps.newHashMap();

    static {
        MAPPING.put(107, 0x1D);
        MAPPING.put(108, 0x1D);
        MAPPING.put(109, 0x1D);
        MAPPING.put(110, 0x1D);
        MAPPING.put(210, 0x1D);
        MAPPING.put(315, 0x1D);
        MAPPING.put(316, 0x1D);
        MAPPING.put(335, 0x20);
        MAPPING.put(338, 0x20);
        MAPPING.put(340, 0x20);
        MAPPING.put(393, 0x2A);
        MAPPING.put(401, 0x2A);
    }

    private Hand hand;

    public UseItem(final Hand hand) {
        this.hand = hand;
    }

    public UseItem() {
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(final Hand hand) {
        this.hand = hand;
    }

    @Override
    public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        hand = Hand.getHandByID(readVarInt(buf));
    }

    @Override
    public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
        writeVarInt(hand.getProtocolId(), buf);
    }

    @Override
    public String toString() {
        return "UseItem{" +
                "hand=" + hand +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UseItem useItem = (UseItem) o;
        return hand == useItem.hand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hand);
    }
}
