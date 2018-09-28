package de.exceptionflug.protocolize.api.protocol;

import de.exceptionflug.protocolize.api.event.EventManager;
import de.exceptionflug.protocolize.api.traffic.TrafficManager;

public final class ProtocolAPI {

    private static final PacketRegistration PACKET_REGISTRATION = new PacketRegistration();
    private static final EventManager EVENT_MANAGER = new EventManager();
    private static final TrafficManager TRAFFIC_MANAGER = new TrafficManager();

    private ProtocolAPI() {}

    public static PacketRegistration getPacketRegistration() {
        return PACKET_REGISTRATION;
    }

    public static EventManager getEventManager() {
        return EVENT_MANAGER;
    }

    public static TrafficManager getTrafficManager() {
        return TRAFFIC_MANAGER;
    }
}
