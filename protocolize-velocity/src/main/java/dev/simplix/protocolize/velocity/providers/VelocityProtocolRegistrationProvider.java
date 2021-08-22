package dev.simplix.protocolize.velocity.providers;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.StateRegistry;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.protocol.ProtocolIdMapping;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.velocity.ProtocolizePlugin;
import dev.simplix.protocolize.velocity.packet.VelocityProtocolizePacket;
import dev.simplix.protocolize.velocity.util.ProtocolizeNamingPolicy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

/**
 * Date: 22.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public class VelocityProtocolRegistrationProvider implements ProtocolRegistrationProvider {

    private final Multimap<Map.Entry<PacketDirection, Class<? extends AbstractPacket>>, ProtocolIdMapping> packets = ArrayListMultimap.create();

    private Constructor<StateRegistry.PacketMapping> packetMappingConstructor;
    private Method registerMethod;

    {
        try {
            packetMappingConstructor = StateRegistry.PacketMapping.class.getDeclaredConstructor(int.class, ProtocolVersion.class, ProtocolVersion.class, boolean.class);
            packetMappingConstructor.setAccessible(true);
            registerMethod = StateRegistry.PacketRegistry.class.getDeclaredMethod("register", Class.class, Supplier.class, StateRegistry.PacketMapping[].class);
            registerMethod.setAccessible(true);
        } catch (Exception exception) {
            log.error("Exception occurred while initializing VelocityProtocolRegistrationProvider:", exception);
        }
    }

    @Override
    public void registerPacket(Collection<ProtocolIdMapping> mappings, Protocol protocol,
                               PacketDirection direction, Class<? extends AbstractPacket> packetClass) {
        Preconditions.checkNotNull(mappings, "Mapping cannot be null");
        Preconditions.checkNotNull(protocol, "Protocol cannot be null");
        Preconditions.checkNotNull(direction, "Direction cannot be null");
        Preconditions.checkNotNull(packetClass, "Packet class cannot be null");
        try {
            ProtocolUtils.Direction velocityDirection = velocityDirection(direction);
            if (velocityDirection == null) {
                return;
            }
            StateRegistry stateRegistry = velocityProtocol(protocol);
            if (stateRegistry == null) {
                return;
            }
            List<StateRegistry.PacketMapping> velocityMappings = new ArrayList<>();
            for (ProtocolIdMapping mapping : mappings) {
                packets.put(new AbstractMap.SimpleEntry<>(direction, packetClass), mapping);
                velocityMappings.add(createVelocityMapping(mapping));
            }
            StateRegistry.PacketRegistry registry = direction == PacketDirection.SERVERBOUND ? stateRegistry.serverbound : stateRegistry.clientbound;
            Class<? extends MinecraftPacket> velocityPacket = generateVelocityPacket(packetClass);
            doRegisterPacket(registry, velocityPacket, velocityMappings.toArray(new StateRegistry.PacketMapping[0]));
        } catch (Exception exception) {
            log.error("Exception while registering packet "+packetClass.getName(), exception);
        }
    }

    private void doRegisterPacket(StateRegistry.PacketRegistry registry, Class<? extends MinecraftPacket> velocityPacket,
                                  StateRegistry.PacketMapping[] mappings) throws InvocationTargetException, IllegalAccessException {
        registerMethod.invoke(registry, velocityPacket, (Supplier<Object>) () -> {
            try {
                return velocityPacket.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Unable to construct instance of "+velocityPacket.getName(), e);
            }
        }, mappings);
    }

    private StateRegistry.PacketMapping createVelocityMapping(ProtocolIdMapping mapping) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return packetMappingConstructor.newInstance(mapping.id(),
                ProtocolVersion.getProtocolVersion(mapping.protocolRangeStart()),
                ProtocolVersion.getProtocolVersion(mapping.protocolRangeEnd()),
                true);
    }

    @Override
    public int packetId(Object packet, Protocol protocol, PacketDirection direction, int protocolVersion) {
        Preconditions.checkNotNull(protocol, "Protocol cannot be null");
        Preconditions.checkNotNull(direction, "Direction cannot be null");
        Preconditions.checkNotNull(packet, "Packet cannot be null");
        if (packet instanceof VelocityProtocolizePacket) {
            Collection<ProtocolIdMapping> protocolIdMappings = packets.get(new AbstractMap.SimpleEntry<>(direction,
                    ((VelocityProtocolizePacket) packet).obtainProtocolizePacketClass()));
            for (ProtocolIdMapping mapping : protocolIdMappings) {
                if (mapping.inRange(protocolVersion)) {
                    return mapping.id();
                }
            }
        } else if (packet instanceof MinecraftPacket) {
            ProtocolUtils.Direction velocityDirection = velocityDirection(direction);
            if (velocityDirection == null) {
                return -1;
            }
            StateRegistry stateRegistry = velocityProtocol(protocol);
            if (stateRegistry == null) {
                return -1;
            }
            StateRegistry.PacketRegistry.ProtocolRegistry registry = velocityDirection.getProtocolRegistry(stateRegistry,
                    ProtocolVersion.getProtocolVersion(protocolVersion));
            return registry.getPacketId((MinecraftPacket) packet);
        }
        return -1;
    }

    private Class<? extends MinecraftPacket> generateVelocityPacket(Class<? extends AbstractPacket> c) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(VelocityProtocolizePacket.class);
        enhancer.setNamingPolicy(new ProtocolizeNamingPolicy(c));
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            if (method.getDeclaringClass().equals(VelocityProtocolizePacket.class)
                    && method.getName().equals("obtainProtocolizePacketClass")) {
                return c;
            }
            return proxy.invokeSuper(obj, args);
        });
        VelocityProtocolizePacket packet = (VelocityProtocolizePacket) enhancer.create();
        if (!packet.obtainProtocolizePacketClass().equals(c)) {
            throw new IllegalStateException("Sanity check failed for dynamic enhanced class " + packet.getClass().getName());
        }
        return packet.getClass();
    }

    private ProtocolUtils.Direction velocityDirection(PacketDirection direction) {
        switch (direction) {
            case CLIENTBOUND:
                return ProtocolUtils.Direction.CLIENTBOUND;
            case SERVERBOUND:
                return ProtocolUtils.Direction.SERVERBOUND;
        }
        return null;
    }

    private StateRegistry velocityProtocol(Protocol protocol) {
        switch (protocol) {
            case LOGIN:
                return StateRegistry.LOGIN;
            case HANDSHAKE:
                return StateRegistry.HANDSHAKE;
            case STATUS:
                return StateRegistry.STATUS;
            case PLAY:
                return StateRegistry.PLAY;
        }
        return null;
    }

}
