package dev.simplix.protocolize.api.packet;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Date: 24.11.2021
 *
 * @author Exceptionflug
 */
@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class RegisteredPacket {

    private final Protocol protocol;
    private final PacketDirection direction;
    private final Class<? extends AbstractPacket> packetClass;

}
