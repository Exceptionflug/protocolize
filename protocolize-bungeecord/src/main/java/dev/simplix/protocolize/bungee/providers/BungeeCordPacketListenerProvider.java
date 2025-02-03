package dev.simplix.protocolize.bungee.providers;

import com.google.common.base.Preconditions;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.providers.AbstractPacketListenerProvider;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;
import dev.simplix.protocolize.bungee.packet.BungeeCordProtocolizePacket;
import dev.simplix.protocolize.bungee.util.ReflectionUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.*;
import java.util.logging.Level;

/**
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
public final class BungeeCordPacketListenerProvider extends AbstractPacketListenerProvider {

    private static final ProtocolizePlayerProvider PLAYER_PROVIDER = Protocolize.playerProvider();

    @Override
    protected void addListener(AbstractPacketListener<?> listener) {
        if (!AbstractPacket.class.isAssignableFrom(listener.type()) && !DefinedPacket.class.isAssignableFrom(listener.type())) {
            throw new IllegalStateException("The packet type is not a valid packet type. Allowed: AbstractPacket and DefinedPacket");
        }
        super.addListener(listener);
    }

    /**
     * Called by the ProtocolizeEncoderChannelHandler before a packet gets sent.
     *
     * @param packet                the outgoing packet
     * @param abstractPacketHandler the bungee packet handler for this packet
     * @return maybe manipulated packet instance
     */
    public DefinedPacket handleOutboundPacket(DefinedPacket packet, AbstractPacketHandler abstractPacketHandler) {
        Preconditions.checkNotNull(packet, "The packet cannot be null!");
        Preconditions.checkNotNull(abstractPacketHandler, "The abstractPacketHandler cannot be null!");
        Class<?> clazz;
        Object apiPacket;
        if (packet instanceof BungeeCordProtocolizePacket) {
            clazz = ((BungeeCordProtocolizePacket) packet).obtainProtocolizePacketClass();
            apiPacket = ((BungeeCordProtocolizePacket) packet).wrapper();
        } else {
            clazz = packet.getClass();
            apiPacket = packet;
        }
        List<AbstractPacketListener<?>> listeners = listenersForType(clazz);
        boolean sentToServer = ReflectionUtil.downstreamBridgeClass.isInstance(abstractPacketHandler) ||
            ReflectionUtil.serverConnectorClass.isInstance(abstractPacketHandler);
        Connection connection = ReflectionUtil.getConnection(abstractPacketHandler, sentToServer);
        if (connection == null) {
            return packet;
        }
        PacketSendEvent event = createSendEvent(connection, abstractPacketHandler, apiPacket);
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
                ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Exception caught in listener while sending packet " + apiPacket.getClass().getName(), e);
            }
        });
        if (event.cancelled())
            return null;
        Object fPacket = event.packet();
        if (packet instanceof BungeeCordProtocolizePacket) {
            ((BungeeCordProtocolizePacket) packet).wrapper((AbstractPacket) fPacket);
        } else {
            packet = (DefinedPacket) fPacket;
        }
        return packet;
    }

    /**
     * Called by the ProtocolizeDecoderChannelHandler when a packet arrives.
     *
     * @param packet                the incoming packet
     * @param abstractPacketHandler the bungee packet handler for this packet
     * @return key value pair with the resulting packet instance and a boolean determining if the packet needs to be rewritten again.
     */
    public Map.Entry<DefinedPacket, Boolean> handleInboundPacket(DefinedPacket packet, AbstractPacketHandler abstractPacketHandler) {
        Preconditions.checkNotNull(packet, "The packet cannot be null!");
        Preconditions.checkNotNull(abstractPacketHandler, "The abstractPacketHandler cannot be null!");
        Class<?> clazz;
        Object apiPacket;
        if (packet instanceof BungeeCordProtocolizePacket) {
            clazz = ((BungeeCordProtocolizePacket) packet).obtainProtocolizePacketClass();
            apiPacket = ((BungeeCordProtocolizePacket) packet).wrapper();
        } else {
            clazz = packet.getClass();
            apiPacket = packet;
        }
        List<AbstractPacketListener<?>> listeners = listenersForType(clazz);
        final boolean sentByServer = ReflectionUtil.downstreamBridgeClass.isInstance(abstractPacketHandler) ||
            ReflectionUtil.serverConnectorClass.isInstance(abstractPacketHandler);
        final Connection connection = ReflectionUtil.getConnection(abstractPacketHandler, sentByServer);
        if (connection == null) {
            return null;
        }
        PacketReceiveEvent event = createReceiveEvent(connection, abstractPacketHandler, apiPacket);
        listeners.stream().sorted(Comparator.comparingInt(AbstractPacketListener::priority)).filter(it -> {
            Direction stream = it.direction();
            if (stream == Direction.DOWNSTREAM && sentByServer) {
                return true;
            } else return stream == Direction.UPSTREAM && !sentByServer;
        }).forEach(it -> {
            try {
                it.packetReceive(event);
            } catch (final Exception e) {
                ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Exception caught in listener while receiving packet " + apiPacket.getClass().getName(), e);
            }
        });
        if (event.cancelled())
            return null;
        Object fPacket = event.packet();
        if (packet instanceof BungeeCordProtocolizePacket) {
            ((BungeeCordProtocolizePacket) packet).wrapper((AbstractPacket) fPacket);
        } else {
            packet = (DefinedPacket) fPacket;
        }
        return new AbstractMap.SimpleEntry<>(packet, event.dirty());
    }

    private PacketReceiveEvent<?> createReceiveEvent(Connection connection, AbstractPacketHandler abstractPacketHandler, Object packet) {
        ProxiedPlayer player = null;
        ServerInfo serverInfo = null;
        if (connection instanceof PendingConnection) {
            player = player(connection);
            if (player != null) {
                Server server = player.getServer();
                if (server != null) {
                    serverInfo = server.getInfo();
                }
            }
        } else if (connection instanceof ProxiedPlayer) {
            player = (ProxiedPlayer) connection;
            serverInfo = player.getServer() != null ? player.getServer().getInfo() : null;
        } else {
            serverInfo = ReflectionUtil.getServerInfo(abstractPacketHandler);
        }
        return new PacketReceiveEvent<>(player != null ? PLAYER_PROVIDER.player(player.getUniqueId()) : null, serverInfo, packet, false, false);
    }

    private PacketSendEvent<?> createSendEvent(Connection connection, AbstractPacketHandler abstractPacketHandler, Object packet) {
        ProxiedPlayer player = null;
        ServerInfo serverInfo = null;
        if (connection instanceof PendingConnection) {
            player = player(connection);
            if (player != null) {
                Server server = player.getServer();
                if (server != null) {
                    serverInfo = server.getInfo();
                }
            }
        } else if (connection instanceof ProxiedPlayer) {
            player = (ProxiedPlayer) connection;
            serverInfo = player.getServer() != null ? player.getServer().getInfo() : null;
        } else {
            serverInfo = ReflectionUtil.getServerInfo(abstractPacketHandler);
        }
        return new PacketSendEvent<>(player != null ? PLAYER_PROVIDER.player(player.getUniqueId()) : null, serverInfo, packet, false);
    }

    private ProxiedPlayer player(Connection connection) {
        if (connection instanceof PendingConnection)
            return ProxyServer.getInstance().getPlayer(((PendingConnection) connection).getUniqueId());
        if (connection instanceof ProxiedPlayer)
            return (ProxiedPlayer) connection;
        return null;
    }

}
