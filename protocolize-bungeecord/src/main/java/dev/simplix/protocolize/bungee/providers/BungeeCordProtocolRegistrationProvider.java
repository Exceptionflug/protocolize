package dev.simplix.protocolize.bungee.providers;

import com.google.common.base.Preconditions;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.packet.RegisteredPacket;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.api.util.ReflectionUtil;
import dev.simplix.protocolize.bungee.packet.BungeeCordProtocolizePacket;
import dev.simplix.protocolize.bungee.strategy.PacketRegistrationStrategy;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;
import net.md_5.bungee.protocol.DefinedPacket;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public final class BungeeCordProtocolRegistrationProvider implements ProtocolRegistrationProvider {

    private final MappingProvider mappingProvider = Protocolize.mappingProvider();
    private PacketRegistrationStrategy strategy = null;

    private Method getIdMethod, createPacketMethod;
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
            createPacketMethod = directionDataClass.getDeclaredMethod("createPacket", int.class, int.class);
            createPacketMethod.setAccessible(true);

            toServerField = net.md_5.bungee.protocol.Protocol.class.getDeclaredField("TO_SERVER");
            toServerField.setAccessible(true);
            toClientField = net.md_5.bungee.protocol.Protocol.class.getDeclaredField("TO_CLIENT");
            toClientField.setAccessible(true);
        } catch (final Exception e) {
            log.error("Exception occurred while initializing BungeeCordProtocolRegistrationProvider: ", e);
        }
    }

    public BungeeCordProtocolRegistrationProvider(List<PacketRegistrationStrategy> strategies) {
        for (PacketRegistrationStrategy strategy : strategies) {
            if (strategy.compatible()) {
                this.strategy = strategy;
                log.info("[Protocolize] Using {}", strategy.getClass().getSimpleName());
                return;
            }
        }
        if (strategy == null) {
            throw new RuntimeException(
                "Unable to find supported PacketRegistrationStrategy. Maybe you are using an unsupported BungeeCord fork.");
        }
    }

    @Override
    public void registerPacket(List<ProtocolIdMapping> mappings, Protocol protocol, PacketDirection direction,
                               Class<? extends AbstractPacket> packetClass) {
        Preconditions.checkNotNull(mappings, "Mapping cannot be null");
        Preconditions.checkNotNull(protocol, "Protocol cannot be null");
        Preconditions.checkNotNull(direction, "Direction cannot be null");
        Preconditions.checkNotNull(packetClass, "Packet class cannot be null");
        try {
            Class<? extends DefinedPacket> definedPacketClass = generateBungeePacket(packetClass);
            Int2ObjectMap<Object> protocols = (Int2ObjectMap<Object>) protocolsField
                .get(getDirectionData(bungeeCordProtocol(protocol), direction));
            for (ProtocolIdMapping mapping : mappings) {
                mappingProvider.registerMapping(new RegisteredPacket(direction, packetClass), mapping);
                for (int i = mapping.protocolRangeStart(); i <= mapping.protocolRangeEnd(); i++) {
                    strategy.registerPacket(protocols, i, mapping.id(), definedPacketClass);
                    log.debug("[Protocolize] Register packet {} (0x{}) in direction {} at protocol {} for version {}", definedPacketClass.getName(), Integer.toHexString(mapping.id()), direction.name(), protocol.name(), i);
                }
            }
        } catch (Exception e) {
            log.warn("Exception while registering packet {}", packetClass.getName(), e);
        }
    }

    @Override
    public void registerItemStructuredComponentType(StructuredComponentType<?> type) {
        Preconditions.checkNotNull(type, "Type cannot be null");
        for (ProtocolIdMapping mapping : type.getMappings()) {
            mappingProvider.registerMapping(type, mapping);
        }
    }

    @Override
    public int packetId(Object packet, Protocol protocol, PacketDirection direction, int protocolVersion) {
        Preconditions.checkNotNull(protocol, "Protocol cannot be null");
        Preconditions.checkNotNull(direction, "Direction cannot be null");
        Preconditions.checkNotNull(packet, "Packet cannot be null");
        if (packet instanceof BungeeCordProtocolizePacket) {
            List<ProtocolIdMapping> protocolIdMappings = mappingProvider.mappings(new RegisteredPacket(
                direction, ((BungeeCordProtocolizePacket) packet).obtainProtocolizePacketClass()));
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

    @SneakyThrows
    @Override
    public Object createPacket(Class<? extends AbstractPacket> clazz, Protocol protocol, PacketDirection direction,
                               int protocolVersion) {
        Preconditions.checkNotNull(protocol, "Protocol cannot be null");
        Preconditions.checkNotNull(direction, "Direction cannot be null");
        Preconditions.checkNotNull(clazz, "Clazz cannot be null");
        Object directionData = getDirectionData(bungeeCordProtocol(protocol), direction);
        ProtocolIdMapping protocolIdMapping = mappingProvider.mapping(new RegisteredPacket(direction, clazz),
            protocolVersion);
        int id;
        if (protocolIdMapping != null) {
            id = protocolIdMapping.id();
        } else {
            id = (int) getIdMethod.invoke(directionData, clazz, protocolVersion);
        }
        return createPacketMethod.invoke(directionData, id, protocolVersion);
    }

    @Override
    public String debugInformation() {
        return "Strategy = " + strategy.getClass().getName() + "\nPacket listing not supported on this platform";
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
            case CONFIGURATION:
                return net.md_5.bungee.protocol.Protocol.CONFIGURATION;
        }
        return null;
    }

    private Class<? extends DefinedPacket> generateBungeePacket(Class<? extends AbstractPacket> c) {
        return new ByteBuddy().subclass(BungeeCordProtocolizePacket.class)
            .method(ElementMatchers.named("obtainProtocolizePacketClass"))
            .intercept(MethodDelegation.to(new ByteBuddyClassInjector(c)))
            .name("dev.simplix.protocolize.bungee.packets.Generated" + c.getSimpleName() + "Wrapper").make()
            .load(getClass().getClassLoader()).getLoaded();
    }

    private Object getDirectionData(net.md_5.bungee.protocol.Protocol protocol, PacketDirection direction) {
        try {
            if (direction == PacketDirection.SERVERBOUND)
                return toServerField.get(protocol);
            else
                return toClientField.get(protocol);
        } catch (final IllegalAccessException e) {
            log.error("Unable to get DirectionData", e);
        }
        return null;
    }

    public PacketRegistrationStrategy strategy() {
        return strategy;
    }

    public static class ByteBuddyClassInjector {

        private final Class<? extends AbstractPacket> packetClass;

        public ByteBuddyClassInjector(Class<? extends AbstractPacket> packetClass) {
            this.packetClass = packetClass;
        }

        @RuntimeType
        public Class<? extends AbstractPacket> obtainProtocolizePacketClass() {
            return packetClass;
        }

    }

}
