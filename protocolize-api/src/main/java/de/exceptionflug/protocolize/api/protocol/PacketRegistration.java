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

    public void registerPlayClientPacket(final Class<? extends AbstractPacket> clazz, final Map<Integer, Integer> protocolIdMapping) {
        registerPacket(Protocol.GAME.TO_CLIENT, clazz, protocolIdMapping);
    }

    public void registerPlayServerPacket(final Class<? extends AbstractPacket> clazz, final Map<Integer, Integer> protocolIdMapping) {
        registerPacket(Protocol.GAME.TO_SERVER, clazz, protocolIdMapping);
    }

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
