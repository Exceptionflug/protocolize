package dev.simplix.protocolize.velocity.util;

import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.StateRegistry;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
public final class ConversionUtils {

    private ConversionUtils() {
    }

    public static ProtocolUtils.Direction velocityDirection(PacketDirection direction) {
        switch (direction) {
            case SERVERBOUND:
                return ProtocolUtils.Direction.SERVERBOUND;
            case CLIENTBOUND:
                return ProtocolUtils.Direction.CLIENTBOUND;
        }
        return null;
    }

    public static StateRegistry velocityProtocol(Protocol protocol) {
        switch (protocol) {
            case LOGIN:
                return StateRegistry.LOGIN;
            case HANDSHAKE:
                return StateRegistry.HANDSHAKE;
            case STATUS:
                return StateRegistry.STATUS;
            case PLAY:
                return StateRegistry.PLAY;
            case CONFIGURATION:
                return StateRegistry.CONFIG;
        }
        return null;
    }

    public static Protocol protocolizeProtocol(StateRegistry state) {
        switch (state) {
            case PLAY:
                return Protocol.PLAY;
            case LOGIN:
                return Protocol.LOGIN;
            case HANDSHAKE:
                return Protocol.HANDSHAKE;
            case STATUS:
                return Protocol.STATUS;
            case CONFIG:
                return Protocol.CONFIGURATION;
        }
        return null;
    }

}
