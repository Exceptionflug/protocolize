package dev.simplix.protocolize.api;

import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.providers.PacketListenerProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides access to the necessary API components. Please be sure
 * to check out the GitHub wiki page about the usage of Protocolize.
 * <br><br>
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
public final class Protocolize {

    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

    static {
        registerService(MappingProvider.class, new SimpleMappingProvider());
    }

    /**
     * This returns an instance of a registered service.
     *
     * @see Protocolize#registerService(Class, Object)
     * @param type The registration type of the desired service
     * @param <T> The type of the service
     * @return The instance of T or null if there is no service with this registration type
     */
    public static <T> T getService(Class<T> type) {
        return (T) SERVICES.get(type);
    }

    /**
     * This will map a given service instance to a given registration type.
     * @param type The registration type of the desired service
     * @param instance An instance of the registration type
     * @param <T> The registration type
     */
    public static <T> void registerService(Class<T> type, T instance) {
        SERVICES.put(type, instance);
    }

    /**
     * This will return the instance of {@link ProtocolRegistrationProvider}. Calling this method is similar to
     * {@code Protocolize.getService(ProtocolRegistrationProvider.class);}
     *
     * @return The instance of {@link ProtocolRegistrationProvider}
     */
    public static ProtocolRegistrationProvider protocolRegistration() {
        return getService(ProtocolRegistrationProvider.class);
    }

    /**
     * This will return the instance of {@link PacketListenerProvider} with which you can register your packet
     * listeners. Calling this method is similar to
     * {@code Protocolize.getService(PacketListenerProvider.class);}
     * @return The instance of {@link PacketListenerProvider}
     */
    public static PacketListenerProvider listenerProvider() {
        return getService(PacketListenerProvider.class);
    }

    /**
     * This will return the instance of {@link MappingProvider}. Calling this method is similar to
     * {@code Protocolize.getService(MappingProvider.class);}
     * @return The instance of {@link MappingProvider}
     */
    public static MappingProvider mappingProvider() {
        return getService(MappingProvider.class);
    }

}
