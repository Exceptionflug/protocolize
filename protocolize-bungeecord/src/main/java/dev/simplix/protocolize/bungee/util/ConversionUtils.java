package dev.simplix.protocolize.bungee.util;

import dev.simplix.protocolize.api.Protocol;

/**
 * Date: 19.11.2023
 *
 * @author Rubenicos
 */
public final class ConversionUtils {

    private ConversionUtils() {
    }

    public static net.md_5.bungee.protocol.Protocol bungeeCordProtocol(Protocol protocol) {
        switch (protocol) {
            case HANDSHAKE:
                return net.md_5.bungee.protocol.Protocol.HANDSHAKE;
            case STATUS:
                return net.md_5.bungee.protocol.Protocol.STATUS;
            case LOGIN:
                return net.md_5.bungee.protocol.Protocol.LOGIN;
            case PLAY:
                return net.md_5.bungee.protocol.Protocol.GAME;
            case CONFIGURATION:
                return net.md_5.bungee.protocol.Protocol.CONFIGURATION;
        }
        return null;
    }

    public static Protocol protocolizeProtocol(net.md_5.bungee.protocol.Protocol protocol) {
        switch (protocol) {
            case GAME:
                return dev.simplix.protocolize.api.Protocol.PLAY;
            case LOGIN:
                return dev.simplix.protocolize.api.Protocol.LOGIN;
            case STATUS:
                return dev.simplix.protocolize.api.Protocol.STATUS;
            case HANDSHAKE:
                return dev.simplix.protocolize.api.Protocol.HANDSHAKE;
            case CONFIGURATION:
                return dev.simplix.protocolize.api.Protocol.CONFIGURATION;
        }
        return null;
    }

}
