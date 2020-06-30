package de.exceptionflug.protocolize.api.util;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.KickStringWriter;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.PacketWrapper;

import java.lang.reflect.Field;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class ReflectionUtil {

    public static Field packetField, bufferField, serverConnectorChannelWrapperField, channelWrapperChannelField, protocolVersionField, userConnectionChannelWrapperField, initialHandlerChannelWrapperField, protocolField, downstreamConnectionField, upstreamConnectionField, serverConnectorUserConnectionField, handlerBossHandlerField, serverConnectionChannelWrapperField, serverConnectorTargetField;

    public static Class<?> serverConnectorClass, downstreamBridgeClass, upstreamBridgeClass, serverConnectionClass;

    static {
        try {
            packetField = PacketWrapper.class.getField("packet");
            packetField.setAccessible(true);
            bufferField = PacketWrapper.class.getField("buf");
            bufferField.setAccessible(true);
            serverConnectorClass = Class.forName("net.md_5.bungee.ServerConnector");
            serverConnectorChannelWrapperField = serverConnectorClass.getDeclaredField("ch");
            serverConnectorTargetField = serverConnectorClass.getDeclaredField("target");
            serverConnectorTargetField.setAccessible(true);
            serverConnectorChannelWrapperField.setAccessible(true);
            serverConnectorUserConnectionField = serverConnectorClass.getDeclaredField("user");
            serverConnectorUserConnectionField.setAccessible(true);
            serverConnectionClass = Class.forName("net.md_5.bungee.ServerConnection");
            serverConnectionChannelWrapperField = serverConnectionClass.getDeclaredField("ch");
            serverConnectionChannelWrapperField.setAccessible(true);
            downstreamBridgeClass = Class.forName("net.md_5.bungee.connection.DownstreamBridge");
            downstreamConnectionField = downstreamBridgeClass.getDeclaredField("con");
            downstreamConnectionField.setAccessible(true);
            upstreamBridgeClass = Class.forName("net.md_5.bungee.connection.UpstreamBridge");
            upstreamConnectionField = upstreamBridgeClass.getDeclaredField("con");
            upstreamConnectionField.setAccessible(true);
            final Class channelWrapperClass = Class.forName("net.md_5.bungee.netty.ChannelWrapper");
            channelWrapperChannelField = channelWrapperClass.getDeclaredField("ch");
            channelWrapperChannelField.setAccessible(true);
            protocolVersionField = MinecraftDecoder.class.getDeclaredField("protocolVersion");
            protocolVersionField.setAccessible(true);
            protocolField = MinecraftDecoder.class.getDeclaredField("protocol");
            protocolField.setAccessible(true);
            final Class userConnectionClass = Class.forName("net.md_5.bungee.UserConnection");
            userConnectionChannelWrapperField = userConnectionClass.getDeclaredField("ch");
            userConnectionChannelWrapperField.setAccessible(true);
            final Class initialHandlerClass = Class.forName("net.md_5.bungee.connection.InitialHandler");
            initialHandlerChannelWrapperField = initialHandlerClass.getDeclaredField("ch");
            initialHandlerChannelWrapperField.setAccessible(true);
            final Class handlerBossClass = Class.forName("net.md_5.bungee.netty.HandlerBoss");
            handlerBossHandlerField = handlerBossClass.getDeclaredField("handler");
            handlerBossHandlerField.setAccessible(true);
        } catch (final Exception e) {
            Logger.getLogger("ReflectionUtil").severe("We have ugly bungeecord implementation right here ._.");
        }
    }

    private ReflectionUtil() {}

    public static Connection getConnection(final AbstractPacketHandler abstractPacketHandler, final boolean sentToServer) {
        final Connection connection;
        try {
            if(abstractPacketHandler instanceof PendingConnection) {
                return (Connection) abstractPacketHandler;
            }
            if(serverConnectorClass.equals(abstractPacketHandler.getClass())) {
                final ProxiedPlayer player = (ProxiedPlayer) serverConnectorUserConnectionField.get(abstractPacketHandler);
                final Object upstreamBridge = getUpstreamBridge(player);
                if(upstreamBridge == null)
                    return null;
                return (Connection) upstreamConnectionField.get(upstreamBridge);
            }
            if(sentToServer)
                connection = (Connection) downstreamConnectionField.get(abstractPacketHandler);
            else
                connection = (Connection) upstreamConnectionField.get(abstractPacketHandler);
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().severe("[Protocolize] Problem while getting connection from "+abstractPacketHandler+" ("+abstractPacketHandler.getClass().getName()+")");
            throw new RuntimeException("Unable to obtain connection", e);
        }
        return connection;
    }

    public static AbstractPacketHandler getUpstreamBridge(final ProxiedPlayer player) {
        Preconditions.checkNotNull(player, "The player cannot be null!");
        try {
            final Object channelWrapper = userConnectionChannelWrapperField.get(player);
            final Channel channel = (Channel) channelWrapperChannelField.get(channelWrapper);
            final Object handlerBoss = channel.pipeline().get("inbound-boss");
            if(handlerBoss == null)
                return null;
            return (AbstractPacketHandler) handlerBossHandlerField.get(handlerBoss);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AbstractPacketHandler getDownstreamBridge(final Server server) {
        Preconditions.checkNotNull(server, "The server cannot be null!");
        try {
            final Object channelWrapper = serverConnectionChannelWrapperField.get(server);
            final Channel channel = (Channel) channelWrapperChannelField.get(channelWrapper);
            final Object handlerBoss = channel.pipeline().get("inbound-boss");
            return (AbstractPacketHandler) handlerBossHandlerField.get(handlerBoss);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getConnectionName(final Connection connection) {
        if(connection instanceof ProxiedPlayer) {
            return ((ProxiedPlayer) connection).getName();
        } else if(connection instanceof PendingConnection) {
            return ((PendingConnection) connection).getName();
        } else if(connection instanceof Server) {
            return ((Server) connection).getInfo().getName();
        }
        return "UNSUPPORTED_CONNECTION_TYPE";
    }

    public static String getServerName(final Connection connection) {
        if(connection instanceof ProxiedPlayer) {
            final Server server = ((ProxiedPlayer) connection).getServer();
            return server != null ? server.getInfo().getName() : "<NO SERVER>";
        } else if(connection instanceof PendingConnection) {
            return "<NO SERVER>";
        } else if(connection instanceof Server) {
            return ((Server) connection).getInfo().getName();
        }
        return "UNSUPPORTED_CONNECTION_TYPE";
    }

    public static Object getChannelWrapper(final Connection connection) throws IllegalAccessException {
        final Object channelWrapper;
        if(connection instanceof ProxiedPlayer) {
            channelWrapper = userConnectionChannelWrapperField.get(connection);
        } else if(connection instanceof Server) {
            channelWrapper = serverConnectionChannelWrapperField.get(connection);
        } else if(connection instanceof PendingConnection) {
            channelWrapper = initialHandlerChannelWrapperField.get(connection);
        } else {
            channelWrapper = null;
        }
        return channelWrapper;
    }

    public static int getProtocolVersion(final Connection player) {
        try {
            final Object channelWrapper = getChannelWrapper(player);
            final Channel channel = (Channel) channelWrapperChannelField.get(channelWrapper);
            MinecraftDecoder minecraftDecoder = channel.pipeline().get(MinecraftDecoder.class);
            if(minecraftDecoder == null) {
                return -1;
            }
            return (int) protocolVersionField.get(minecraftDecoder);
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static ServerInfo getServerInfo(final AbstractPacketHandler packetHandler) {
        if(packetHandler.getClass().equals(serverConnectorClass)) {
            try {
                return (ServerInfo) serverConnectorTargetField.get(packetHandler);
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static KickStringWriter getKickStringWriter() throws Exception {
        Field field = PipelineUtils.class.getDeclaredField("legacyKicker");
        field.setAccessible(true);
        return (KickStringWriter) field.get(null);
    }
}
