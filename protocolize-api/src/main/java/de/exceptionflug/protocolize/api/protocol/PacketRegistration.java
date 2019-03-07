package de.exceptionflug.protocolize.api.protocol;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;

/**
 * This class provides access to the access protected BungeeCord protocol implementation.
 */
public final class PacketRegistration {

    private Constructor protocolMappingConstructor;
    private Method registerMethod, getIdMethod;
    private Class<?> mappingClass, directionData;
    private Field toServerField, toClientField;

    {
        try {
            mappingClass = Class.forName("net.md_5.bungee.protocol.Protocol$ProtocolMapping");
            directionData = Class.forName("net.md_5.bungee.protocol.Protocol$DirectionData");
            protocolMappingConstructor = mappingClass.getConstructor(int.class, int.class);
            protocolMappingConstructor.setAccessible(true);
            registerMethod = directionData.getDeclaredMethod("registerPacket", Class.class, Array.newInstance(mappingClass, 0).getClass());
            registerMethod.setAccessible(true);
            getIdMethod = directionData.getDeclaredMethod("getId", Class.class, int.class);
            getIdMethod.setAccessible(true);

            toServerField = Protocol.class.getDeclaredField("TO_SERVER");
            toServerField.setAccessible(true);
            toClientField = Protocol.class.getDeclaredField("TO_CLIENT");
            toClientField.setAccessible(true);
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE,"Exception occurred while initializing PacketRegistration: ", e);
        }
    }

    /**
     * This method registers a {@link AbstractPacket} for the PLAY / GAME protocol in direction to the client. This method is equal to
     * {@code registerPacket(Protocol.GAME.TO_CLIENT, clazz, protocolIdMapping);}
     * @see PacketRegistration#registerPacket(Protocol, Direction, Class, Map)
     * @param clazz the class of the packet
     * @param protocolIdMapping a map containing the protocol versions and their corresponding packet id.
     */
    public void registerPlayClientPacket(final Class<? extends AbstractPacket> clazz, final Map<Integer, Integer> protocolIdMapping) {
        registerPacket(Protocol.GAME, Direction.TO_CLIENT, clazz, protocolIdMapping);
    }

    /**
     * This method registers a {@link AbstractPacket} for the PLAY / GAME protocol in direction to the client. This method is equal to
     * {@code registerPacket(Protocol.GAME.TO_SERVER, clazz, protocolIdMapping);}
     * @see PacketRegistration#registerPacket(Protocol, Direction, Class, Map)
     * @param clazz the class of the packet
     * @param protocolIdMapping a map containing the protocol versions and their corresponding packet id.
     */
    public void registerPlayServerPacket(final Class<? extends AbstractPacket> clazz, final Map<Integer, Integer> protocolIdMapping) {
        registerPacket(Protocol.GAME, Direction.TO_SERVER, clazz, protocolIdMapping);
    }

    /**
     * This method registers a {@link AbstractPacket}.
     * @param protocol the protocol.
     * @param direction the protocol direction
     * @param clazz the class of the packet
     * @param protocolIdMapping a map containing the protocol versions and their corresponding packet id.
     */
    public void registerPacket(final Protocol protocol, final Direction direction, final Class<? extends AbstractPacket> clazz, final Map<Integer, Integer> protocolIdMapping) {
        Preconditions.checkNotNull(clazz, "The clazz cannot be null!");
        Preconditions.checkNotNull(protocol, "The protocol cannot be null!");
        Preconditions.checkNotNull(direction, "The direction cannot be null!");
        Preconditions.checkNotNull(protocolIdMapping, "The protocolIdMapping cannot be null!");
        try {
            for(final Integer protocolVersion : protocolIdMapping.keySet()) {
                final Object map = protocolMappingConstructor.newInstance(protocolVersion, protocolIdMapping.get(protocolVersion));
                final Object mapArray = Array.newInstance(mappingClass, 1);
                Array.set(mapArray, 0, map);
                registerMethod.invoke(getDirectionData(protocol, direction), clazz, mapArray);
            }
            ProxyServer.getInstance().getLogger().info("[Protocolize] Injected custom packet: "+clazz.getName());
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception occurred while registering packet: "+clazz.getName(), e);
        }
    }

    /**
     * Returns a packet id for the given protocol and it's version.
     * @see de.exceptionflug.protocolize.api.util.ProtocolVersions
     * @param protocol the protocol.
     * @param direction the protocol direction
     * @param protocolVersion the protocol version
     * @param clazz the class of the packet
     * @return the packet id or -1 if the packet is not registered in that specific direction
     */
    public int getPacketID(final Protocol protocol, final Direction direction, final int protocolVersion, final Class<? extends DefinedPacket> clazz) {
        Preconditions.checkNotNull(clazz, "The clazz cannot be null!");
        Preconditions.checkNotNull(protocol, "The protocol cannot be null!");
        Preconditions.checkNotNull(direction, "The direction cannot be null!");
        final Object data = getDirectionData(protocol, direction);
        try {
            return (int) getIdMethod.invoke(data, clazz, protocolVersion);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            if(e.getCause() != null && e.getCause() instanceof IllegalArgumentException) {
                try {
                    return (int) getIdMethod.invoke(data, clazz, protocolVersion);
                } catch (final IllegalAccessException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return -1;
    }

    private Object getDirectionData(final Protocol protocol, final Direction direction) {
        try {
            if(direction == Direction.TO_SERVER)
                return toServerField.get(protocol);
            else
                return toClientField.get(protocol);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
