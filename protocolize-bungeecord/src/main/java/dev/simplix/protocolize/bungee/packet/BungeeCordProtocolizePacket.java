package dev.simplix.protocolize.bungee.packet;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.util.DebugUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.Objects;

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
    private byte[] skipBuffer;

    public BungeeCordProtocolizePacket() {
        Class<? extends AbstractPacket> wrapperClass = obtainProtocolizePacketClass();
        if (wrapperClass == null) {
            throw new RuntimeException("Unable to determine protocolize packet type.");
        }
        try {
            wrapper = wrapperClass.getConstructor().newInstance();
        } catch (Exception exception) {
            throw new RuntimeException("Unable to construct instance of " + wrapperClass.getName() + ". Please ensure that the "
                + "default constructor is existent and accessible.");
        }
    }

    public Class<? extends AbstractPacket> obtainProtocolizePacketClass() {
        return null; // Will be overridden by ByteBuddy
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        try {
            if (Protocolize.listenerProvider().listenersForType(obtainProtocolizePacketClass()).isEmpty()) {
                skipBuffer = new byte[buf.readableBytes()];
                buf.readBytes(skipBuffer); // Don't decode packet if nobody has interest in it
                return;
            }
            wrapper.read(buf, direction == ProtocolConstants.Direction.TO_CLIENT ? PacketDirection.CLIENTBOUND : PacketDirection.SERVERBOUND,
                protocolVersion);
            if (buf.isReadable() && DebugUtil.enabled) {
                DebugUtil.writeDump(buf, new CorruptedFrameException("Protocolize is unable to read packet " + obtainProtocolizePacketClass().getName()
                    + " at protocol version " + protocolVersion + " in direction " + direction.name()));
            }
            if (Objects.equals(System.getProperty("protocolize.reset.readerindex"), "true")) {
                buf.resetReaderIndex();
            }
        } catch (Throwable throwable) {
            BadPacketException badPacketException = new BadPacketException("Protocolize is unable to read packet " + obtainProtocolizePacketClass().getName()
                + " at protocol version " + protocolVersion + " in direction " + direction.name(), throwable);
            if (DebugUtil.enabled) {
                DebugUtil.writeDump(buf, badPacketException);
            }
            throw badPacketException;
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (skipBuffer != null) {
            buf.writeBytes(skipBuffer);
            return;
        }
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
