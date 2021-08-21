package dev.simplix.protocolize.bungee.providers;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.protocol.ProtocolIdMapping;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.api.util.ReflectionUtil;
import dev.simplix.protocolize.bungee.packet.BungeeCordProtocolizePacket;
import dev.simplix.protocolize.bungee.strategy.PacketRegistrationStrategy;
import dev.simplix.protocolize.bungee.util.ProtocolizeNamingPolicy;
import gnu.trove.map.TIntObjectMap;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

/**
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
public final class BungeeCordProtocolRegistrationProvider implements ProtocolRegistrationProvider {

    private final Multimap<Map.Entry<PacketDirection, Class<? extends AbstractPacket>>, ProtocolIdMapping> packets = ArrayListMultimap.create();
    private PacketRegistrationStrategy strategy = null;

    private Method getIdMethod;
    private Field toServerField, toClientField, protocolsField;

    {
        try {
            Class<?> mappingClass = ReflectionUtil.getClass("net.md_5.bungee.protocol.Protocol$ProtocolMapping");
            Class<?> directionDataClass = ReflectionUtil.getClass("net.md_5.bungee.protocol.Protocol$DirectionData");
            Class<?> protocolDataClass = ReflectionUtil.getClass("net.md_5.bungee.protocol.Protocol$ProtocolData");
            Constructor<?> protocolMappingConstructor = mappingClass.getConstructor(int.class, int.class);
            protocolMappingConstructor.setAccessible(true);
            protocolsField = ReflectionUtil.field(directionDataClass, "protocols");
            protocolsField.setAccessible(true);
            getIdMethod = directionDataClass.getDeclaredMethod("getId", Class.class, int.class);
            getIdMethod.setAccessible(true);

            toServerField = net.md_5.bungee.protocol.Protocol.class.getDeclaredField("TO_SERVER");
            toServerField.setAccessible(true);
            toClientField = net.md_5.bungee.protocol.Protocol.class.getDeclaredField("TO_CLIENT");
            toClientField.setAccessible(true);
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception occurred while initializing BungeeCordProtocolRegistrationProvider: ", e);
        }
    }

    public BungeeCordProtocolRegistrationProvider(List<PacketRegistrationStrategy> strategies) {
        for (PacketRegistrationStrategy strategy : strategies) {
            if (strategy.compatible()) {
                this.strategy = strategy;
                ProxyServer.getInstance().getLogger().info("[Protocolize] Using " + strategy.getClass().getSimpleName());
                return;
            }
        }
        if (strategy == null) {
            throw new RuntimeException("Unable to find supported PacketRegistrationStrategy. Maybe you are using an unsupported BungeeCord fork.");
        }
    }

    @Override
    public void registerPacket(Collection<ProtocolIdMapping> mappings, Protocol protocol, PacketDirection direction, Class<? extends AbstractPacket> packetClass) {
        Preconditions.checkNotNull(mappings, "Mapping cannot be null");
        Preconditions.checkNotNull(protocol, "Protocol cannot be null");
        Preconditions.checkNotNull(direction, "Direction cannot be null");
        Preconditions.checkNotNull(packetClass, "Packet class cannot be null");
        try {
            Class<? extends DefinedPacket> definedPacketClass = generateBungeePacket(packetClass);
            TIntObjectMap<Object> protocols = (TIntObjectMap<Object>) protocolsField.get(getDirectionData(bungeeCordProtocol(protocol), direction));
            for (ProtocolIdMapping mapping : mappings) {
                packets.put(new AbstractMap.SimpleEntry<>(direction, packetClass), mapping);
                for (int i = mapping.protocolRangeStart(); i <= mapping.protocolRangeEnd(); i++) {
                    strategy.registerPacket(protocols, i, mapping.id(), definedPacketClass);
                }
            }
        } catch (Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Exception while registering packet "+packetClass.getName(), e);
        }
    }

    @Override
    public int packetId(Object packet, Protocol protocol, PacketDirection direction, int protocolVersion) {
        Preconditions.checkNotNull(protocol, "Protocol cannot be null");
        Preconditions.checkNotNull(direction, "Direction cannot be null");
        Preconditions.checkNotNull(packet, "Packet cannot be null");
        if (packet instanceof BungeeCordProtocolizePacket) {
            Collection<ProtocolIdMapping> protocolIdMappings = packets.get(new AbstractMap.SimpleEntry<>(direction,
                    ((BungeeCordProtocolizePacket) packet).obtainProtocolizePacketClass()));
            for (ProtocolIdMapping mapping : protocolIdMappings) {
                if (mapping.inRange(protocolVersion)) {
                    return mapping.id();
                }
            }
        } else {
            Object data = getDirectionData(bungeeCordProtocol(protocol), direction);
            try {
                return (int) getIdMethod.invoke(data, packet.getClass(), protocolVersion);
            } catch (final IllegalAccessException | InvocationTargetException e) {
                if (e.getCause() != null && e.getCause() instanceof IllegalArgumentException) {
                    try {
                        return (int) getIdMethod.invoke(data, packet.getClass(), protocolVersion);
                    } catch (final IllegalAccessException | InvocationTargetException e1) {
                    }
                }
            }
        }
        return -1;
    }

    private net.md_5.bungee.protocol.Protocol bungeeCordProtocol(Protocol protocol) {
        switch (protocol) {
            case HANDSHAKE:
                return net.md_5.bungee.protocol.Protocol.HANDSHAKE;
            case STATUS:
                return net.md_5.bungee.protocol.Protocol.STATUS;
            case LOGIN:
                return net.md_5.bungee.protocol.Protocol.LOGIN;
            case PLAY:
                return net.md_5.bungee.protocol.Protocol.GAME;
        }
        return null;
    }

    private Class<? extends DefinedPacket> generateBungeePacket(Class<? extends AbstractPacket> c) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(BungeeCordProtocolizePacket.class);
        enhancer.setNamingPolicy(new ProtocolizeNamingPolicy(c));
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            if (method.getDeclaringClass().equals(BungeeCordProtocolizePacket.class)
                    && method.getName().equals("obtainProtocolizePacketClass")) {
                return c;
            }
            return proxy.invokeSuper(obj, args);
        });
        BungeeCordProtocolizePacket packet = (BungeeCordProtocolizePacket) enhancer.create();
        if (!packet.obtainProtocolizePacketClass().equals(c)) {
            throw new IllegalStateException("Insanity check failed for dynamic enhanced class " + packet.getClass().getName());
        }
        return packet.getClass();
    }

    private Object getDirectionData(net.md_5.bungee.protocol.Protocol protocol, PacketDirection direction) {
        try {
            if (direction == PacketDirection.SERVERBOUND)
                return toServerField.get(protocol);
            else
                return toClientField.get(protocol);
        } catch (final IllegalAccessException e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Unable to get DirectionData", e);
        }
        return null;
    }

    public PacketRegistrationStrategy strategy() {
        return strategy;
    }
}
