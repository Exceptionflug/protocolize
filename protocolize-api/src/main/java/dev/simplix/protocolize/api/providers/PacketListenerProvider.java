package dev.simplix.protocolize.api.providers;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;

import java.util.List;

/**
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
public interface PacketListenerProvider {

    default <T> AbstractPacketListener<T> registerListener(Class<T> packet, Direction direction) {
        return registerListener(packet, direction, 0);
    }

    default <T> AbstractPacketListener<T> registerListener(Class<T> packet, Direction direction, int priority) {
        return registerListener(new AbstractPacketListener<T>(packet, direction, priority) { });
    }

    <T, A extends AbstractPacketListener<T>> A registerListener(A listener);

    <T, A extends AbstractPacketListener<T>> A unregisterListener(A listener) throws IllegalArgumentException;

    List<AbstractPacketListener<?>> listenersForType(Class<?> type);

    String debugInformation();
}
