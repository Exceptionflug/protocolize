package dev.simplix.protocolize.velocity.packet;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
            throw new RuntimeException("Unable to construct instance of "+ wrapperClass.getName()+". Please ensure that the "
                    + "default constructor is existent and accessible.");
        }
    }

    public Class<? extends AbstractPacket> obtainProtocolizePacketClass() {
        return null; // Will be overridden by cglib
    }

    @Override
    public void decode(ByteBuf byteBuf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        wrapper.read(byteBuf,
                direction == ProtocolUtils.Direction.CLIENTBOUND ? PacketDirection.CLIENTBOUND : PacketDirection.SERVERBOUND,
                protocolVersion.getProtocol());
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
