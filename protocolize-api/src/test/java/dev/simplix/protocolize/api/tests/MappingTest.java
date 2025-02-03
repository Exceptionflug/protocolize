package dev.simplix.protocolize.api.tests;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.packet.RegisteredPacket;
import io.netty.buffer.ByteBuf;
import org.junit.Test;

import java.util.List;

/**
 * Date: 24.11.2021
 *
 * @author Exceptionflug
 */
public class MappingTest {

    @Test
    public void packetMappingTest() {
        Protocolize.mappingProvider().registerMapping(new RegisteredPacket(Protocol.PLAY, PacketDirection.SERVERBOUND, TestPacket.class),
            AbstractProtocolMapping.rangedIdMapping(47, 756, 0x3D));
        Protocolize.mappingProvider().registerMapping(new RegisteredPacket(Protocol.PLAY, PacketDirection.SERVERBOUND, TestPacket.class),
            AbstractProtocolMapping.rangedIdMapping(23, 46, 0x2D));
        Protocolize.mappingProvider().registerMapping(new RegisteredPacket(Protocol.PLAY, PacketDirection.SERVERBOUND, AnotherTestPacket.class),
            AbstractProtocolMapping.rangedIdMapping(47, 756, 0x3D));

        List<ProtocolMapping> mappings = Protocolize.mappingProvider().mappings(new RegisteredPacket(Protocol.PLAY, PacketDirection.SERVERBOUND, TestPacket.class));
        assert !mappings.isEmpty();
        ProtocolMapping mapping1 = mappings.get(0);
        ProtocolMapping mapping2 = mappings.get(1);
        assert mapping1.inRange(47);
        assert mapping2.inRange(24);

        ProtocolMapping mapping = Protocolize.mappingProvider().mapping(new RegisteredPacket(Protocol.PLAY, PacketDirection.SERVERBOUND, AnotherTestPacket.class), 47);
        assert mapping != null;
        assert mapping.inRange(47);

        System.out.println(Protocolize.mappingProvider().debugInformation());
    }

    final static class TestPacket extends AbstractPacket {

        @Override
        public void read(ByteBuf buf, PacketDirection direction, int protocolVersion) {

        }

        @Override
        public void write(ByteBuf buf, PacketDirection direction, int protocolVersion) {

        }
    }

    final static class AnotherTestPacket extends AbstractPacket {

        @Override
        public void read(ByteBuf buf, PacketDirection direction, int protocolVersion) {

        }

        @Override
        public void write(ByteBuf buf, PacketDirection direction, int protocolVersion) {

        }
    }

}
