package de.exceptionflug.protocolize.api.handler;

import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.event.PacketSendEvent;
import net.md_5.bungee.protocol.DefinedPacket;

/**
 * A PacketListener interface is used to listen for packets of a specific type. It uses {@link net.md_5.bungee.event.EventPriority} to determine the call order when multiple listeners
 * of the same type are registered.
 * @param <T> the packet type
 */
public interface PacketListener<T extends DefinedPacket> {

    /**
     * Called when a desired packet arrives at the stream
     * @param event the event containing the information
     */
    void receive(PacketReceiveEvent<T> event);

    /**
     * Called when a desired packet wants to leave the stream
     * @param event the event containing the information
     */
    void send(PacketSendEvent<T> event);

    Stream getStream();
    Class<T> getPacketClass();
    byte getPriority();

}
