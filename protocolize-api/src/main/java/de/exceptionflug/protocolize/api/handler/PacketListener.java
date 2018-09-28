package de.exceptionflug.protocolize.api.handler;

import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.event.PacketSendEvent;
import net.md_5.bungee.protocol.DefinedPacket;

public interface PacketListener<T extends DefinedPacket> {

    void receive(PacketReceiveEvent<T> event);
    void send(PacketSendEvent<T> event);

    Stream getStream();
    Class<T> getPacketClass();

}
