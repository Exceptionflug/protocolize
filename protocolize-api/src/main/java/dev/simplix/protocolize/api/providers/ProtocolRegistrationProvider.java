package dev.simplix.protocolize.api.providers;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;

import java.util.List;

/**
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
public interface ProtocolRegistrationProvider {

    void registerPacket(List<ProtocolIdMapping> mappings, Protocol protocol,
                        PacketDirection direction, Class<? extends AbstractPacket> packetClass);

    int packetId(Object packet, Protocol protocol, PacketDirection direction, int protocolVersion);

    Object createPacket(Class<? extends AbstractPacket> clazz, Protocol protocol, PacketDirection direction, int protocolVersion);

}
