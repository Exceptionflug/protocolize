package dev.simplix.protocolize.velocity.providers;

import com.google.common.base.Preconditions;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.providers.PacketListenerProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 22.08.2021
 *
 * @author Exceptionflug
 */
public class VelocityPacketListenerProvider implements PacketListenerProvider {

    private final List<AbstractPacketListener<?>> listeners = new ArrayList<>();

    @Override
    public void registerListener(AbstractPacketListener<?> listener) {
        Preconditions.checkNotNull(listener, "The listener cannot be null!");
        if (listeners.contains(listener)) {
            throw new IllegalStateException("Listener already registered.");
        }
        if (!AbstractPacket.class.isAssignableFrom(listener.type()) && !MinecraftPacket.class.isAssignableFrom(listener.type())) {
            throw new IllegalStateException("The packet type is not a valid packet type. Allowed: AbstractPacket and MinecraftPacket");
        }
        listeners.add(listener);
    }

    @Override
    public void unregisterListener(AbstractPacketListener<?> listener) throws IllegalArgumentException {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        } else {
            throw new IllegalArgumentException("Did not find " + listener.getClass().getName() + " as a registered listener");
        }
    }

}
