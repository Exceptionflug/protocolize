package de.exceptionflug.protocolize.api.protocol;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.Protocol.DirectionData;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.lang.reflect.*;
import java.util.Map;
import java.util.logging.Level;

/**
 * This class provides access to the access protected BungeeCord protocol implementation.
 */
public final class PacketRegistration {

    private Constructor protocolMappingConstructor;
    private Method registerMethod, getIdMethod;
    private Class<?> mappingClass;

    {
        try {
            mappingClass = Class.forName("net.md_5.bungee.protocol.Protocol$ProtocolMapping");
            protocolMappingConstructor = mappingClass.getConstructor(int.class, int.class);
            protocolMappingConstructor.setAccessible(true);
            registerMethod = DirectionData.class.getDeclaredMethod("registerPacket", Class.class, Array.newInstance(mappingClass, 0).getClass());
            registerMethod.setAccessible(true);
            getIdMethod = DirectionData.class.getDeclaredMethod("getId", Class.class, int.class);
            getIdMethod.setAccessible(true);
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE,"Exception occurred while initializing PacketRegistration: ", e);
        }
    }

    /**
     * This method registers a {@link AbstractPacket} for the PLAY / GAME protocol in direction to the client. This method is equal to
     * {@code registerPacket(Protocol.GAME.TO_CLIENT, clazz, protocolIdMapping);}
     * @see PacketRegistration#registerPacket(DirectionData, Class, Map)
     * @param clazz the class of the packet
     * @param protocolIdMapping a map containing the protocol versions and their corresponding packet id.
     */
    public void registerPlayClientPacket(final Class<? extends AbstractPacket> clazz, final Map<Integer, Integer> protocolIdMapping) {
        registerPacket(Protocol.GAME.TO_CLIENT, clazz, protocolIdMapping);
    }

    /**
     * This method registers a {@link AbstractPacket} for the PLAY / GAME protocol in direction to the client. This method is equal to
     * {@code registerPacket(Protocol.GAME.TO_SERVER, clazz, protocolIdMapping);}
     * @see PacketRegistration#registerPacket(DirectionData, Class, Map)
     * @param clazz the class of the packet
     * @param protocolIdMapping a map containing the protocol versions and their corresponding packet id.
     */
    public void registerPlayServerPacket(final Class<? extends AbstractPacket> clazz, final Map<Integer, Integer> protocolIdMapping) {
        registerPacket(Protocol.GAME.TO_SERVER, clazz, protocolIdMapping);
    }

    /**
     * This method registers a {@link AbstractPacket}.
     * @param data the protocol and direction. May be {@code Protocol.GAME.TO_SERVER} or {@code Protocol.GAME.TO_CLIENT}.
     * @param clazz the class of the packet
     * @param protocolIdMapping a map containing the protocol versions and their corresponding packet id.
     */
    public void registerPacket(final DirectionData data, final Class<? extends AbstractPacket> clazz, final Map<Integer, Integer> protocolIdMapping) {
        Preconditions.checkNotNull(clazz, "The clazz cannot be null!");
        Preconditions.checkNotNull(data, "The data cannot be null!");
        Preconditions.checkNotNull(protocolIdMapping, "The protocolIdMapping cannot be null!");
        try {
            final Object mapArray = Array.newInstance(mappingClass, protocolIdMapping.size());
            int index = 0;
            for(final Integer protocolVersion : protocolIdMapping.keySet()) {
                final Object map = protocolMappingConstructor.newInstance(protocolVersion, protocolIdMapping.get(protocolVersion));
                Array.set(mapArray, index, map);
                index ++;
            }
            registerMethod.invoke(data, clazz, mapArray);
            ProxyServer.getInstance().getLogger().info("[Protocolize] Injected custom packet: "+clazz.getName());
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception occurred while registering packet: "+clazz.getName(), e);
        }
    }

    /**
     * Returns a packet id for the given protocol and it's version.
     * @see de.exceptionflug.protocolize.api.util.ProtocolVersions
     * @param data the protocol and direction. May be {@code Protocol.GAME.TO_SERVER} or {@code Protocol.GAME.TO_CLIENT}.
     * @param protocolVersion the protocol version
     * @param clazz the class of the packet
     * @return the packet id or -1 if the packet is not registered in that specific direction
     */
    public int getPacketID(final DirectionData data, final int protocolVersion, final Class<? extends DefinedPacket> clazz) {
        try {
            return (int) getIdMethod.invoke(data, clazz, protocolVersion);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            if(e.getCause() != null && e.getCause() instanceof IllegalArgumentException) {
                try {
                    if(data.getDirection() == Direction.TO_CLIENT) {
                        return (int) getIdMethod.invoke(Protocol.GAME.TO_CLIENT, clazz, protocolVersion);
                    } else {
                        return (int) getIdMethod.invoke(Protocol.GAME.TO_SERVER, clazz, protocolVersion);
                    }
                } catch (final IllegalAccessException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return -1;
    }

}
