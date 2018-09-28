package de.exceptionflug.protocolize.api.handler;

import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.event.PacketSendEvent;
import net.md_5.bungee.protocol.DefinedPacket;

public class PacketAdapter<T extends DefinedPacket> implements PacketListener<T> {

    private final Stream stream;
    private final Class<T> packet;

    public PacketAdapter(final Stream stream, final Class<T> packet) {
        this.stream = stream;
        this.packet = packet;
    }

    public void receive(final PacketReceiveEvent<T> event) {
        // To be overwritten
    }

    public void send(final PacketSendEvent<T> event) {
        // To be overwritten
    }

    public Stream getStream() {
        return stream;
    }

    public Class<T> getPacketClass() {
        return packet;
    }

}
