package dev.simplix.protocolize.velocity.providers;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.StateRegistry;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.packet.RegisteredPacket;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.velocity.packet.VelocityProtocolizePacket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Date: 22.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public final class VelocityProtocolRegistrationProvider implements ProtocolRegistrationProvider {

    private final MappingProvider mappingProvider = Protocolize.mappingProvider();
    private Constructor<StateRegistry.PacketMapping> packetMappingConstructor;
    private Field versionsField;
    private Field packetClassToIdField;
    private Method registerMethod;

    {
        try {
            packetMappingConstructor = StateRegistry.PacketMapping.class.getDeclaredConstructor(int.class, ProtocolVersion.class, ProtocolVersion.class, boolean.class);
            packetMappingConstructor.setAccessible(true);
            registerMethod = StateRegistry.PacketRegistry.class.getDeclaredMethod("register", Class.class, Supplier.class, StateRegistry.PacketMapping[].class);
            registerMethod.setAccessible(true);
            versionsField = StateRegistry.PacketRegistry.class.getDeclaredField("versions");
            versionsField.setAccessible(true);
            packetClassToIdField = StateRegistry.PacketRegistry.ProtocolRegistry.class.getDeclaredField("packetClassToId");
            packetClassToIdField.setAccessible(true);
        } catch (Exception exception) {
            log.error("Exception occurred while initializing VelocityProtocolRegistrationProvider:", exception);
        }
    }

    @Override
    public void registerPacket(List<ProtocolIdMapping> mappings, Protocol protocol,
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
            StateRegistry.PacketRegistry registry = direction == PacketDirection.SERVERBOUND ? stateRegistry.serverbound : stateRegistry.clientbound;
            Class<? extends MinecraftPacket> velocityPacket = generateVelocityPacket(packetClass);
            List<StateRegistry.PacketMapping> velocityMappings = new ArrayList<>();

            final int maximumProtocolVersion = ProtocolVersion.MAXIMUM_VERSION.getProtocol();
            var sortedMappings = mappings.stream()
                .filter(mapping -> maximumProtocolVersion >= mapping.protocolRangeStart())
                .sorted(Comparator.comparingInt(ProtocolMapping::protocolRangeEnd))
                .toArray(ProtocolIdMapping[]::new);

            for (int i = 0; i < sortedMappings.length; i++) {
                var mapping = sortedMappings[i];
                mappingProvider.registerMapping(new RegisteredPacket(direction, packetClass), mapping);
                if (mapping.protocolRangeEnd() > maximumProtocolVersion) {
                    mapping.protocolRangeEnd(maximumProtocolVersion);
                }
                velocityMappings.add(createVelocityMapping(mapping.protocolRangeStart(), mapping.protocolRangeEnd(), mapping.id(), i == sortedMappings.length - 1));
            }
            try {
                doRegisterPacket(registry, velocityPacket, velocityMappings.toArray(new StateRegistry.PacketMapping[0]));
            } catch (InvocationTargetException e) {
                if (e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().contains("already registered")) {
                    log.debug(e.getCause().getMessage() + ". Skipping...");
                } else {
                    throw e;
                }
            }
        } catch (Exception exception) {
            log.error("Exception while registering packet " + packetClass.getName(), exception);
        }
    }

    private void doRegisterPacket(StateRegistry.PacketRegistry registry, Class<? extends MinecraftPacket> velocityPacket,
                                  StateRegistry.PacketMapping[] mappings) throws InvocationTargetException, IllegalAccessException {
        registerMethod.invoke(registry, velocityPacket, (Supplier<Object>) () -> {
            try {
                return velocityPacket.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Unable to construct instance of " + velocityPacket.getName(), e);
            }
        }, mappings);
    }

    private StateRegistry.PacketMapping createVelocityMapping(int start, int end, int id, boolean lastValid) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return packetMappingConstructor.newInstance(id,
            ProtocolVersion.getProtocolVersion(start),
            lastValid ? ProtocolVersion.getProtocolVersion(end) : null,
            false);
    }

    @Override
    public int packetId(Object packet, Protocol protocol, PacketDirection direction, int protocolVersion) {
        Preconditions.checkNotNull(protocol, "Protocol cannot be null");
        Preconditions.checkNotNull(direction, "Direction cannot be null");
        Preconditions.checkNotNull(packet, "Packet cannot be null");
        if (packet instanceof VelocityProtocolizePacket) {
            Class<? extends AbstractPacket> packetClass = ((VelocityProtocolizePacket) packet).obtainProtocolizePacketClass();
            ProtocolIdMapping protocolIdMapping = mappingProvider.mapping(new RegisteredPacket(direction,
                packetClass), protocolVersion);
            if (protocolIdMapping != null) {
                return protocolIdMapping.id();
            } else {
                log.debug("Unable to obtain id for " + direction.name() + " " + packetClass.getName() + " at protocol " + protocolVersion);
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

    @Override
    public Object createPacket(Class<? extends AbstractPacket> clazz, Protocol protocol, PacketDirection direction, int protocolVersion) {
        ProtocolUtils.Direction velocityDirection = velocityDirection(direction);
        if (velocityDirection == null) {
            log.debug("Unable to construct wrapper instance for " + clazz.getName() + ": Unknown packet direction to velocity: " + direction.name());
            return null;
        }
        StateRegistry stateRegistry = velocityProtocol(protocol);
        if (stateRegistry == null) {
            log.debug("Unable to construct wrapper instance for " + clazz.getName() + ": Unknown protocol: " + protocol.name());
            return null;
        }
        StateRegistry.PacketRegistry.ProtocolRegistry registry = velocityDirection.getProtocolRegistry(stateRegistry,
            ProtocolVersion.getProtocolVersion(protocolVersion));
        ProtocolIdMapping protocolIdMapping = mappingProvider.mapping(new RegisteredPacket(direction, clazz), protocolVersion);
        if (protocolIdMapping != null) {
            return registry.createPacket(protocolIdMapping.id());
        }
        log.debug("No protocol id mapping for " + clazz.getName() + " at version " + protocolVersion + " was found.");
        return null;
    }

    @Override
    public String debugInformation() {
        StringBuilder builder = new StringBuilder("Generated export of " + getClass().getName() + ":\n\n");
        builder.append(examineDirection(ProtocolUtils.Direction.CLIENTBOUND)).append("\n");
        builder.append(examineDirection(ProtocolUtils.Direction.SERVERBOUND)).append("\n");
        return builder.toString();
    }

    @SneakyThrows
    private String examineDirection(ProtocolUtils.Direction direction) {
        StringBuilder builder = new StringBuilder(direction.name() + ":\n");
        for (StateRegistry stateRegistry : StateRegistry.values()) {
            builder.append("  - ").append(stateRegistry.name()).append(":\n");
            for (ProtocolVersion version : ProtocolVersion.SUPPORTED_VERSIONS) {
                builder.append("    - ").append("Minecraft ").append(version.getVersionsSupportedBy()).append(":\n");
                StateRegistry.PacketRegistry.ProtocolRegistry registry = direction.getProtocolRegistry(stateRegistry, version);
                Map<Class<? extends MinecraftPacket>, Integer> packetMap = (Map<Class<? extends MinecraftPacket>, Integer>) packetClassToIdField.get(registry);
                for (Class<? extends MinecraftPacket> clazz : packetMap.keySet()) {
                    builder.append("      - ").append(clazz.getName()).append(" id = 0x").append(Integer.toHexString(packetMap.get(clazz))).append("\n");
                }
            }
        }
        return builder.toString();
    }

    private Class<? extends MinecraftPacket> generateVelocityPacket(Class<? extends AbstractPacket> c) {
        return new ByteBuddy()
            .subclass(VelocityProtocolizePacket.class)
            .method(ElementMatchers.named("obtainProtocolizePacketClass"))
            .intercept(MethodDelegation.to(new ByteBuddyClassInjector(c)))
            .name("dev.simplix.protocolize.velocity.packets.Generated" + c.getSimpleName() + "Wrapper")
            .make()
            .load(getClass().getClassLoader())
            .getLoaded();
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
