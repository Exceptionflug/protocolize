package dev.simplix.protocolize.bungee.netty;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.PacketListenerProvider;
import dev.simplix.protocolize.bungee.ProtocolizePlugin;
import dev.simplix.protocolize.bungee.providers.BungeeCordPacketListenerProvider;
import dev.simplix.protocolize.bungee.util.CancelSendSignal;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.KeepAlive;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public final class ProtocolizeEncoderChannelHandler extends MessageToMessageEncoder<DefinedPacket> {

    private static final BungeeCordPacketListenerProvider LISTENER_PROVIDER = (BungeeCordPacketListenerProvider) Protocolize.getService(PacketListenerProvider.class);
    private final AbstractPacketHandler abstractPacketHandler;

    public ProtocolizeEncoderChannelHandler(final AbstractPacketHandler abstractPacketHandler) {
        this.abstractPacketHandler = abstractPacketHandler;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, DefinedPacket msg, final List<Object> out) throws Exception {
        msg = LISTENER_PROVIDER.handleOutboundPacket(msg, abstractPacketHandler);
        if (msg != null)
            out.add(msg);
        else
            out.add(new KeepAlive(ThreadLocalRandom.current().nextLong())); // We need to produce at least one message
    }

    @SuppressWarnings("deprecation")
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (cause.getClass().equals(CancelSendSignal.INSTANCE.getClass()))
            throw ((Error) cause);
        if (ProtocolizePlugin.isExceptionCausedByProtocolize(cause)) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Exception caught in encoder.", cause);
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

}
