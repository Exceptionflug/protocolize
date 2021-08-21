package dev.simplix.protocolize.api.providers;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.protocol.ProtocolIdMapping;

import java.util.Collection;

/**
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
public interface ProtocolRegistrationProvider {

    void registerPacket(Collection<ProtocolIdMapping> mappings, Protocol protocol,
                        PacketDirection direction, Class<? extends AbstractPacket> packetClass);

    int packetId(Object packet, Protocol protocol, PacketDirection direction, int protocolVersion);

}
