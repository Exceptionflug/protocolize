package dev.simplix.protocolize.bungee.packet;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

/**
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Setter
@Accessors(fluent = true)
public class BungeeCordProtocolizePacket extends DefinedPacket {

    private AbstractPacket wrapper;

    public BungeeCordProtocolizePacket() {
        Class<? extends AbstractPacket> wrapperClass = obtainProtocolizePacketClass();
        if (wrapperClass == null) {
            throw new RuntimeException("Unable to determine protocolize packet type.");
        }
        try {
            wrapper = wrapperClass.getConstructor().newInstance();
        } catch (Exception exception) {
            throw new RuntimeException("Unable to construct instance of "+ wrapperClass.getName()+". Please ensure that the "
                + "default constructor is existent and accessible.");
        }
    }

    public Class<? extends AbstractPacket> obtainProtocolizePacketClass() {
        return null; // Will be overridden by cglib
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        wrapper.read(buf, direction == ProtocolConstants.Direction.TO_CLIENT ? PacketDirection.CLIENTBOUND : PacketDirection.SERVERBOUND,
                protocolVersion);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        wrapper.write(buf, direction == ProtocolConstants.Direction.TO_CLIENT ? PacketDirection.CLIENTBOUND : PacketDirection.SERVERBOUND,
                protocolVersion);
    }

    @Override
    public void handle(AbstractPacketHandler abstractPacketHandler) throws Exception {
    }

    @Override
    public boolean equals(Object o) {
        return wrapper.equals(o);
    }

    @Override
    public int hashCode() {
        return wrapper.hashCode();
    }

    @Override
    public String toString() {
        return wrapper.toString();
    }

}
