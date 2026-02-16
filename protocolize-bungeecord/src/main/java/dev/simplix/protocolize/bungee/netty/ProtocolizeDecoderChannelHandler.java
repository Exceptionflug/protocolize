package dev.simplix.protocolize.bungee.netty;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.PacketListenerProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.bungee.ProtocolizePlugin;
import dev.simplix.protocolize.bungee.providers.BungeeCordPacketListenerProvider;
import dev.simplix.protocolize.bungee.util.CancelSendSignal;
import dev.simplix.protocolize.bungee.util.ConversionUtils;
import dev.simplix.protocolize.bungee.util.ReflectionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.unix.Errors.NativeIoException;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.protocol.*;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

public final class ProtocolizeDecoderChannelHandler extends MessageToMessageDecoder<PacketWrapper> {

    private static final String VERSION = ProxyServer.getInstance().getPluginManager().getPlugin("Protocolize").getDescription().getVersion();
    private static final BungeeCordPacketListenerProvider LISTENER_PROVIDER = (BungeeCordPacketListenerProvider) Protocolize.getService(PacketListenerProvider.class);
    private static final ProtocolRegistrationProvider REGISTRATION_PROVIDER = Protocolize.getService(ProtocolRegistrationProvider.class);
    private final AbstractPacketHandler abstractPacketHandler;
    private final Connection connection;
    private final dev.simplix.protocolize.api.Direction streamDirection;
    private Direction direction;
    private Protocol protocol;
    private int protocolVersion;

    public ProtocolizeDecoderChannelHandler(AbstractPacketHandler abstractPacketHandler, dev.simplix.protocolize.api.Direction streamDirection) {
        this.abstractPacketHandler = abstractPacketHandler;
        connection = ReflectionUtil.getConnection(abstractPacketHandler, ReflectionUtil.downstreamBridgeClass.isInstance(abstractPacketHandler));
        this.streamDirection = streamDirection;
        try {
            if (ReflectionUtil.isServerConnector(abstractPacketHandler)) {
                direction = Direction.TO_CLIENT;
                final Object ch = ReflectionUtil.serverConnectorChannelWrapperField.get(abstractPacketHandler);
                final Channel channel = (Channel) ReflectionUtil.channelWrapperChannelField.get(ch);
                final MinecraftDecoder minecraftDecoder = channel.pipeline().get(MinecraftDecoder.class);
                protocolVersion = (int) ReflectionUtil.protocolVersionField.get(minecraftDecoder);
                protocol = (Protocol) ReflectionUtil.protocolField.get(minecraftDecoder);
            } else {
                if (ReflectionUtil.isInitialHandler(abstractPacketHandler)) {
                    final Object ch = ReflectionUtil.initialHandlerChannelWrapperField.get(abstractPacketHandler);
                    final Channel channel = (Channel) ReflectionUtil.channelWrapperChannelField.get(ch);
                    final MinecraftDecoder minecraftDecoder = channel.pipeline().get(MinecraftDecoder.class);
                    protocolVersion = (int) ReflectionUtil.protocolVersionField.get(minecraftDecoder);
                    protocol = (Protocol) ReflectionUtil.protocolField.get(minecraftDecoder);
                } else if (ReflectionUtil.downstreamBridgeClass.isInstance(abstractPacketHandler)) {
                    final Object ch = ReflectionUtil.userConnectionChannelWrapperField.get(connection);
                    final Channel channel = (Channel) ReflectionUtil.channelWrapperChannelField.get(ch);
                    final MinecraftDecoder minecraftDecoder = channel.pipeline().get(MinecraftDecoder.class);
                    protocolVersion = (int) ReflectionUtil.protocolVersionField.get(minecraftDecoder);
                    protocol = (Protocol) ReflectionUtil.protocolField.get(minecraftDecoder);
                } else {
                    throw new IllegalStateException("Unsupported packet handler: " + abstractPacketHandler.getClass().getName());
                }
                direction = Direction.TO_SERVER;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final PacketWrapper msg, final List<Object> out) throws Exception {
        if (msg != null) {
            if (msg.packet != null) {
                // Packet handling & rewrite
                final Entry<DefinedPacket, Boolean> entry = LISTENER_PROVIDER.handleInboundPacket(msg.packet, abstractPacketHandler);
                if (entry == null)
                    return;
                final DefinedPacket packet = entry.getKey();
                if (packet == null)
                    return;
                if (entry.getValue()) {
                    try {
                        // Try packet rewrite
                        final ByteBuf buf = Unpooled.directBuffer();
                        int packetID = REGISTRATION_PROVIDER.packetId(packet, ConversionUtils.protocolizeProtocol(protocol),
                            direction == Direction.TO_CLIENT ? PacketDirection.CLIENTBOUND : PacketDirection.SERVERBOUND,
                            protocolVersion);
                        if (packetID != -1) {
                            DefinedPacket.writeVarInt(packetID, buf);
                            packet.write(buf, direction, protocolVersion);
                            msg.buf.resetReaderIndex();
                            buf.resetReaderIndex();
                            ReflectionUtil.bufferField.set(msg, buf);
                        }
                    } catch (final UnsupportedOperationException ignored) {
                    } // Packet cannot be written
                }
                ReflectionUtil.packetField.set(msg, packet);
                out.add(msg);
            } else {
                out.add(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (cause.getClass().equals(CancelSendSignal.INSTANCE.getClass()))
            throw ((Error) cause);
        if (cause instanceof ClosedChannelException) {
            return;
        } else if (cause instanceof NativeIoException) {
            return; // Suppress this annoying shit...
        }
        if (ProtocolizePlugin.isExceptionCausedByProtocolize(cause) && !(cause instanceof BadPacketException)) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] === EXCEPTION CAUGHT IN DECODER ===");
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Protocolize " + VERSION);
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Stream Direction: " + streamDirection.name());
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Connection: " + connection.toString());
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Protocol version: " + protocolVersion);
            cause.printStackTrace();
        } else {
            super.exceptionCaught(ctx, cause); // We don't argue with foreign exceptions anymore.
        }
    }

}
