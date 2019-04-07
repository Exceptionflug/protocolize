package de.exceptionflug.protocolize.netty;

import de.exceptionflug.protocolize.ProtocolizePlugin;
import de.exceptionflug.protocolize.api.CancelSendSignal;
import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.PingPacket;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class ProtocolizeEncoderChannelHandler extends MessageToMessageEncoder<DefinedPacket> {

    private final AbstractPacketHandler abstractPacketHandler;

    public ProtocolizeEncoderChannelHandler(final AbstractPacketHandler abstractPacketHandler) {
        this.abstractPacketHandler = abstractPacketHandler;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, DefinedPacket msg, final List<Object> out) throws Exception {
        msg = ProtocolAPI.getEventManager().handleOutboundPacket(msg, abstractPacketHandler);
        if(msg != null)
            out.add(msg);
        else
            out.add(new KeepAlive(ThreadLocalRandom.current().nextLong())); // We need to produce at least one message
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if(cause.getClass().equals(CancelSendSignal.INSTANCE.getClass()))
            throw ((Error)cause);
        if(ProtocolizePlugin.isExceptionCausedByProtocolize(cause)) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Exception caught in encoder.", cause);
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

}
