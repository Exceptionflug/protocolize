package dev.simplix.protocolize.api;

import dev.simplix.protocolize.api.providers.PacketListenerProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
public final class Protocolize {

    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

    public static <T> T getService(Class<T> type) {
        return (T) SERVICES.get(type);
    }

    public static <T> void registerService(Class<T> type, T instance) {
        SERVICES.put(type, instance);
    }

    public static ProtocolRegistrationProvider protocolRegistration() {
        return getService(ProtocolRegistrationProvider.class);
    }

    public static PacketListenerProvider listenerProvider() {
        return getService(PacketListenerProvider.class);
    }
}
