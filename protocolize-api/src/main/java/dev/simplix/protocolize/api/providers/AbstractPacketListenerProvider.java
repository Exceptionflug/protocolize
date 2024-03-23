package dev.simplix.protocolize.api.providers;

import com.google.common.base.Preconditions;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 19.11.2023
 *
 * @author Rubenicos
 */
public abstract class AbstractPacketListenerProvider  implements PacketListenerProvider {

    protected final List<AbstractPacketListener<?>> listeners = new ArrayList<>();

    @Override
    public <T, A extends AbstractPacketListener<T>> A registerListener(A listener) {
        Preconditions.checkNotNull(listener, "The listener cannot be null!");
        if (listeners.contains(listener)) {
            throw new IllegalStateException("Listener already registered.");
        }
        addListener(listener);
        return listener;
    }

    protected void addListener(AbstractPacketListener<?> listener) {
        listeners.add(listener);
        listener.registered(true);
    }

    @Override
    public <T, A extends AbstractPacketListener<T>> A unregisterListener(A listener) throws IllegalArgumentException {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
            listener.registered(false);
            return listener;
        } else {
            throw new IllegalArgumentException("Did not find " + listener.getClass().getName() + " as a registered listener");
        }
    }

    @Override
    public String debugInformation() {
        StringBuilder builder = new StringBuilder("Generated export of " + getClass().getName() + ":\n\n");
        for (AbstractPacketListener<?> listener : listeners) {
            builder.append(" - ").append(listener.getClass().getName()).append(" listening for ")
                .append(listener.type().getName()).append(" on ").append(listener.direction().name())
                .append(" with priority ").append(listener.priority()).append("\n");
        }
        return builder.toString();
    }

    @Override
    public List<AbstractPacketListener<?>> listenersForType(Class<?> clazz) {
        Preconditions.checkNotNull(clazz, "The clazz cannot be null!");
        List<AbstractPacketListener<?>> out = new ArrayList<>();
        for (AbstractPacketListener<?> listener : listeners) {
            if (listener.type().equals(clazz)) {
                out.add(listener);
            }
        }
        return out;
    }
}
