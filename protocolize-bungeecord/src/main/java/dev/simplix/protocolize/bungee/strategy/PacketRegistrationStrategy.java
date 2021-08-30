package dev.simplix.protocolize.bungee.strategy;

import gnu.trove.map.TIntObjectMap;

/**
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
public interface PacketRegistrationStrategy {

    void registerPacket(TIntObjectMap<Object> protocols, int protocolVersion, int packetId, Class<?> clazz) throws IllegalAccessException;
    boolean compatible();

}
