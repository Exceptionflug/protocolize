package dev.simplix.protocolize.velocity.packet;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.util.DebugUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.checkerframework.checker.units.qual.C;

/**
 * Date: 22.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Setter
@Accessors(fluent = true)
public class VelocityProtocolizePacket implements MinecraftPacket {

    private AbstractPacket wrapper;

    public VelocityProtocolizePacket() {
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
    public void decode(ByteBuf byteBuf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        try {
            wrapper.read(byteBuf,
                direction == ProtocolUtils.Direction.CLIENTBOUND ? PacketDirection.CLIENTBOUND : PacketDirection.SERVERBOUND,
                protocolVersion.getProtocol());
        } catch (Throwable throwable) {
            CorruptedFrameException corruptedFrameException = new CorruptedFrameException("Protocolize is unable to read packet " + obtainProtocolizePacketClass().getName()
                + " at protocol version " + protocolVersion + " in direction " + direction.name(), throwable);
            DebugUtil.writeDump(byteBuf, corruptedFrameException);
            throw corruptedFrameException;
        }
    }

    @Override
    public void encode(ByteBuf byteBuf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        wrapper.write(byteBuf,
            direction == ProtocolUtils.Direction.CLIENTBOUND ? PacketDirection.CLIENTBOUND : PacketDirection.SERVERBOUND,
            protocolVersion.getProtocol());
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return false;
    }

}
