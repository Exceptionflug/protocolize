package de.exceptionflug.protocolize.netty;

import de.exceptionflug.protocolize.api.CancelSendSignal;
import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.List;
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
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if(cause.getClass().equals(CancelSendSignal.INSTANCE.getClass()))
            throw ((Error)cause);
        ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Exception caught in encoder.", cause);
    }

}
