package dev.simplix.protocolize.velocity.providers;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.StateRegistry;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.providers.PacketListenerProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;
import dev.simplix.protocolize.api.util.ReflectionUtil;
import dev.simplix.protocolize.velocity.packet.VelocityProtocolizePacket;
import io.netty.util.collection.IntObjectMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

/**
 * Date: 22.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public final class VelocityPacketListenerProvider implements PacketListenerProvider {

    private static final ProtocolizePlayerProvider PLAYER_PROVIDER = Protocolize.playerProvider();
    private final ProtocolRegistrationProvider registrationProvider = Protocolize.protocolRegistration();
    private final List<AbstractPacketListener<?>> listeners = new ArrayList<>();

    private Field packetIdToSupplierField;
    private Field serverboundField;
    private Field clientboundField;
    private Method getProtocolRegistryMethod;

    {
        try {
            getProtocolRegistryMethod = StateRegistry.PacketRegistry.class.getDeclaredMethod("getProtocolRegistry", ProtocolVersion.class);
            getProtocolRegistryMethod.setAccessible(true);
            packetIdToSupplierField = StateRegistry.PacketRegistry.ProtocolRegistry.class.getDeclaredField("packetIdToSupplier");
            packetIdToSupplierField.setAccessible(true);
            clientboundField = StateRegistry.class.getDeclaredField("clientbound");
            clientboundField.setAccessible(true);
            serverboundField = StateRegistry.class.getDeclaredField("serverbound");
            serverboundField.setAccessible(true);
        } catch (Exception e) {
            log.error("Unable to initialize VelocityPacketListenerProvider", e);
        }
    }

    @Override
    public void registerListener(AbstractPacketListener<?> listener) {
        Preconditions.checkNotNull(listener, "The listener cannot be null!");
        if (listeners.contains(listener)) {
            throw new IllegalStateException("Listener already registered.");
        }
        if (!AbstractPacket.class.isAssignableFrom(listener.type()) && !MinecraftPacket.class.isAssignableFrom(listener.type())) {
            throw new IllegalStateException("The packet type is not a valid packet type. Allowed: AbstractPacket and MinecraftPacket");
        }
        if (MinecraftPacket.class.isAssignableFrom(listener.type())) {
            try {
                ensureAlsoEncode((Class<? extends MinecraftPacket>) listener.type());
            } catch (Exception e) {
                log.error("Unable to register additional suppliers for velocity packet " + listener.type().getName(), e);
            }
        }
        listeners.add(listener);
    }

    private void ensureAlsoEncode(Class<? extends MinecraftPacket> type) throws Exception {
        for (StateRegistry state : StateRegistry.values()) {
            for (ProtocolVersion protocolVersion : ProtocolVersion.SUPPORTED_VERSIONS) {
                addDefaultSupplier(protocolRegistry((StateRegistry.PacketRegistry) serverboundField.get(state), protocolVersion), type);
                addDefaultSupplier(protocolRegistry((StateRegistry.PacketRegistry) clientboundField.get(state), protocolVersion), type);
            }
        }
    }

    private void addDefaultSupplier(StateRegistry.PacketRegistry.ProtocolRegistry registry, Class<? extends MinecraftPacket> clazz) throws Exception {
        MinecraftPacket packet = clazz.getConstructor().newInstance();
        try {
            int id = registry.getPacketId(packet);
            addDefaultSupplier(registry, clazz, id);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @SneakyThrows
    private StateRegistry.PacketRegistry.ProtocolRegistry protocolRegistry(StateRegistry.PacketRegistry registry, ProtocolVersion protocolVersion) {
        return (StateRegistry.PacketRegistry.ProtocolRegistry) getProtocolRegistryMethod.invoke(registry, protocolVersion);
    }

    private void addDefaultSupplier(StateRegistry.PacketRegistry.ProtocolRegistry registry, Class<? extends MinecraftPacket> clazz, int id) throws IllegalAccessException {
        IntObjectMap<Supplier<? extends MinecraftPacket>> map = (IntObjectMap<Supplier<? extends MinecraftPacket>>) packetIdToSupplierField.get(registry);
        if (map.containsKey(id)) {
            return;
        }
        map.put(id, () -> {
            try {
                return (MinecraftPacket) ReflectionUtil.newInstance(clazz);
            } catch (Exception e) {
                throw new RuntimeException("Unable to dynamically construct packet " + clazz.getName(), e);
            }
        });
    }

    @Override
    public void unregisterListener(AbstractPacketListener<?> listener) throws IllegalArgumentException {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        } else {
            throw new IllegalArgumentException("Did not find " + listener.getClass().getName() + " as a registered listener");
        }
    }

    @Override
    public String debugInformation() {
        StringBuilder builder = new StringBuilder("Generated export of " + getClass().getName() + ":\n\n");
        for (AbstractPacketListener<?> listener : listeners) {
            builder.append(" - ").append(listener.getClass().getName()).append(" listening for ")
                .append(listener.type().getName()).append(" on ").append(listener.direction().name())
                .append(" with priority ").append(listener.priority()).append("\n");
        }
        return builder.toString();
    }

    public Map.Entry<MinecraftPacket, Boolean> handleInboundPacket(MinecraftPacket packet, ServerConnection serverConnection, InboundConnection connection) {
        Preconditions.checkNotNull(packet, "Packet cannot be null");
        boolean sentByServer = serverConnection != null;
        Class<?> clazz;
        Object apiPacket;
        if (packet instanceof VelocityProtocolizePacket) {
            clazz = ((VelocityProtocolizePacket) packet).obtainProtocolizePacketClass();
            apiPacket = ((VelocityProtocolizePacket) packet).wrapper();
        } else {
            clazz = packet.getClass();
            apiPacket = packet;
        }
        List<AbstractPacketListener<?>> listeners = listenersForType(clazz);
        if (listeners.isEmpty()) {
            return new AbstractMap.SimpleEntry<>(packet, false);
        }
        PacketReceiveEvent event = createReceiveEvent(connection, serverConnection, apiPacket);
        listeners.stream().sorted(Comparator.comparingInt(AbstractPacketListener::priority)).filter(it -> {
            Direction stream = it.direction();
            if (stream == Direction.DOWNSTREAM && sentByServer) {
                return true;
            } else if (stream == Direction.UPSTREAM && !sentByServer) {
                return true;
            }
            return false;
        }).forEach(it -> {
            try {
                it.packetReceive(event);
            } catch (final Exception e) {
                log.error("[Protocolize] Exception caught in listener while receiving packet " + apiPacket.getClass().getName(), e);
            }
        });
        if (event.cancelled())
            return null;
        Object fPacket = event.packet();
        if (packet instanceof VelocityProtocolizePacket) {
            ((VelocityProtocolizePacket) packet).wrapper((AbstractPacket) fPacket);
        } else {
            packet = (MinecraftPacket) fPacket;
        }
        return new AbstractMap.SimpleEntry<>(packet, event.dirty());
    }

    private PacketReceiveEvent<?> createReceiveEvent(InboundConnection connection, ServerConnection serverConnection, Object packet) {
        if (serverConnection != null) {
            Player player = serverConnection.getPlayer();
            return new PacketReceiveEvent<>(player != null ? PLAYER_PROVIDER.player(player.getUniqueId()) : null, serverConnection.getServerInfo(), packet, false, false);
        } else {
            Player player = (Player) connection;
            ServerInfo info = player.getCurrentServer().isPresent() ? player.getCurrentServer().get().getServerInfo() : null;
            return new PacketReceiveEvent<>(PLAYER_PROVIDER.player(player.getUniqueId()), info, packet, false, false);
        }
    }

    private List<AbstractPacketListener<?>> listenersForType(Class<?> clazz) {
        Preconditions.checkNotNull(clazz, "The clazz cannot be null!");
        List<AbstractPacketListener<?>> out = new ArrayList<>();
        for (AbstractPacketListener<?> listener : listeners) {
            if (listener.type().equals(clazz)) {
                out.add(listener);
            }
        }
        return out;
    }

    public MinecraftPacket handleOutboundPacket(MinecraftPacket packet, InboundConnection inboundConnection, ServerConnection serverConnection) {
        Preconditions.checkNotNull(packet, "Packet cannot be null");
        Class<?> clazz;
        Object apiPacket;
        if (packet instanceof VelocityProtocolizePacket) {
            clazz = ((VelocityProtocolizePacket) packet).obtainProtocolizePacketClass();
            apiPacket = ((VelocityProtocolizePacket) packet).wrapper();
        } else {
            clazz = packet.getClass();
            apiPacket = packet;
        }
        List<AbstractPacketListener<?>> listeners = listenersForType(clazz);
        if (listeners.isEmpty()) {
            return packet;
        }
        boolean sentToServer = serverConnection != null;
        PacketSendEvent event = createSendEvent(inboundConnection, serverConnection, apiPacket);
        listeners.stream().sorted(Comparator.comparingInt(AbstractPacketListener::priority)).filter(it -> {
            Direction stream = it.direction();
            if (stream == Direction.DOWNSTREAM && sentToServer) {
                return true;
            } else if (stream == Direction.UPSTREAM && !sentToServer) {
                return true;
            }
            return false;
        }).forEach(it -> {
            try {
                it.packetSend(event);
            } catch (final Exception e) {
                log.error("[Protocolize] Exception caught in listener while sending packet " + apiPacket.getClass().getName(), e);
            }
        });
        if (event.cancelled())
            return null;
        Object fPacket = event.packet();
        if (packet instanceof VelocityProtocolizePacket) {
            ((VelocityProtocolizePacket) packet).wrapper((AbstractPacket) fPacket);
        } else {
            packet = (MinecraftPacket) fPacket;
        }
        return packet;
    }

    private PacketSendEvent<?> createSendEvent(InboundConnection connection, ServerConnection serverConnection, Object apiPacket) {
        if (serverConnection != null) {
            Player player = serverConnection.getPlayer();
            return new PacketSendEvent<>(player != null ? PLAYER_PROVIDER.player(player.getUniqueId()) : null, serverConnection.getServerInfo(), apiPacket, false);
        } else if (connection != null) {
            Player player = (Player) connection;
            ServerInfo info = player.getCurrentServer().isPresent() ? player.getCurrentServer().get().getServerInfo() : null;
            return new PacketSendEvent<>(PLAYER_PROVIDER.player(player.getUniqueId()), info, apiPacket, false);
        } else {
            return new PacketSendEvent<>(null, null, apiPacket, false);
        }
    }

}
