package dev.simplix.protocolize.api.providers;

import dev.simplix.protocolize.api.listener.AbstractPacketListener;

/**
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
public interface PacketListenerProvider {

    void registerListener(AbstractPacketListener<?> listener);

    void unregisterListener(AbstractPacketListener<?> listener) throws IllegalArgumentException;

}
