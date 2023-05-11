package dev.simplix.protocolize.api.providers;

import dev.simplix.protocolize.api.listener.AbstractPacketListener;

import java.util.List;

/**
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
public interface PacketListenerProvider {

    void registerListener(AbstractPacketListener<?> listener);

    void unregisterListener(AbstractPacketListener<?> listener) throws IllegalArgumentException;

    List<AbstractPacketListener<?>> listenersForType(Class<?> type);

    String debugInformation();
}
