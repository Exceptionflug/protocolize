package dev.simplix.protocolize.api.packet;

import dev.simplix.protocolize.api.PacketDirection;
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

    private final PacketDirection direction;
    private final Class<? extends AbstractPacket> packetClass;

}
