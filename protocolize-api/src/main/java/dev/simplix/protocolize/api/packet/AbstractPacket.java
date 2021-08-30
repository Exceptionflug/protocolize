package dev.simplix.protocolize.api.packet;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import io.netty.buffer.ByteBuf;

/**
 * This class can be used when implementing custom packets. Please note that custom implemented packets need to be registered by the {@link ProtocolRegistrationProvider}.
 * <br><br>
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
public abstract class AbstractPacket {

    public abstract void read(ByteBuf buf, PacketDirection direction, int protocolVersion);
    public abstract void write(ByteBuf buf, PacketDirection direction, int protocolVersion);

}
