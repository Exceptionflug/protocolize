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

    default <T> AbstractPacketListener<T> register(Class<T> packet, Direction direction) {
        return register(packet, direction, 0);
    }

    default <T> AbstractPacketListener<T> register(Class<T> packet, Direction direction, int priority) {
        return register(new AbstractPacketListener<T>(packet, direction, priority) { });
    }

    <T, A extends AbstractPacketListener<T>> A register(A listener);

    default void registerListener(AbstractPacketListener<?> listener) {
        register(listener);
    }

    <T, A extends AbstractPacketListener<T>> A unregister(A listener) throws IllegalArgumentException;

    default void unregisterListener(AbstractPacketListener<?> listener) throws IllegalArgumentException {
        unregister(listener);
    }

    List<AbstractPacketListener<?>> listenersForType(Class<?> type);

    String debugInformation();
}
