package dev.simplix.protocolize.bungee.strategy;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

/**
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
public interface PacketRegistrationStrategy {

    void registerPacket(Int2ObjectMap<Object> protocols, int protocolVersion, int packetId, Class<?> clazz) throws IllegalAccessException;

    boolean compatible();

}
