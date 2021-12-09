package dev.simplix.protocolize.velocity.netty;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.proxy.connection.MinecraftConnection;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.connection.client.InitialInboundConnection;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.api.util.ReflectionUtil;
import dev.simplix.protocolize.velocity.ProtocolizePlugin;
import dev.simplix.protocolize.velocity.providers.VelocityPacketListenerProvider;
import dev.simplix.protocolize.velocity.util.ConversionUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j(topic = "Protocolize")
@Accessors(fluent = true)
public final class ProtocolizeDecoderChannelHandler extends MessageToMessageDecoder<MinecraftPacket> {

    private static final VelocityPacketListenerProvider LISTENER_PROVIDER = (VelocityPacketListenerProvider) Protocolize.listenerProvider();
    private static final ProtocolRegistrationProvider REGISTRATION_PROVIDER = Protocolize.protocolRegistration();
    private static final Field CONNECTION_FIELD = ReflectionUtil.fieldOrNull(InitialInboundConnection.class, "connection", true);
    private final Direction streamDirection;
    private InboundConnection inboundConnection;
    private ServerConnection serverConnection;
    private PacketDirection packetDirection;
    private Protocol protocol;
    private ProtocolVersion protocolVersion;

    public ProtocolizeDecoderChannelHandler(Direction streamDirection) {
        this.streamDirection = streamDirection;
    }

    public void connection(InboundConnection connection) throws IllegalAccessException {
        this.inboundConnection = connection;
        protocolVersion = connection.getProtocolVersion();
        if (connection instanceof ConnectedPlayer) {
            packetDirection = PacketDirection.SERVERBOUND;
            protocol = ConversionUtils.protocolizeProtocol(((ConnectedPlayer) connection).getConnection().getState());
        } else if (connection instanceof InitialInboundConnection) {
            packetDirection = PacketDirection.SERVERBOUND;
            protocol = ConversionUtils.protocolizeProtocol(((MinecraftConnection) CONNECTION_FIELD.get(connection)).getState());
        } else {
            throw new IllegalArgumentException("Unsupported InboundConnection instance: " + connection.getClass().getName());
        }
    }

    public void connection(ServerConnection connection) {
        this.serverConnection = connection;
        protocolVersion = connection.getPlayer().getProtocolVersion();
        if (connection instanceof VelocityServerConnection) {
            packetDirection = PacketDirection.CLIENTBOUND;
            protocol = ConversionUtils.protocolizeProtocol(((VelocityServerConnection) connection).getConnection().getState());
        } else {
            throw new IllegalArgumentException("Unsupported ServerConnection instance: " + connection.getClass().getName());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, MinecraftPacket minecraftPacket, List<Object> list) throws Exception {
        if (minecraftPacket != null) {
            try {
                if (serverConnection == null && inboundConnection == null) {
                    return;
                }
                Map.Entry<MinecraftPacket, Boolean> entry = LISTENER_PROVIDER.handleInboundPacket(minecraftPacket, serverConnection, inboundConnection);
                if (entry == null) {
                    minecraftPacket = null;
                    return;
                }
                minecraftPacket = entry.getKey();
            } finally {
                if (minecraftPacket != null) {
                    ReferenceCountUtil.retain(minecraftPacket);
                    list.add(minecraftPacket);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (cause instanceof ClosedChannelException) {
            return;
        }
        if (ProtocolizePlugin.isExceptionCausedByProtocolize(cause)) {
            log.error("=== EXCEPTION CAUGHT IN DECODER ===");
            log.error("Protocolize " + ProtocolizePlugin.version());
            log.error("Stream Direction: " + streamDirection.name());
            log.error("InboundConnection: " + inboundConnection + ", ServerConnection: " + serverConnection);
            log.error("Protocol version: " + protocolVersion.getVersionsSupportedBy().toString().replace("[", "").replace("]", ""));
            cause.printStackTrace();
        } else {
            super.exceptionCaught(ctx, cause); // We don't argue with foreign exceptions anymore.
        }
    }

}
