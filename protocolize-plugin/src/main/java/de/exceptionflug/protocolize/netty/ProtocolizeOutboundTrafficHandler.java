package de.exceptionflug.protocolize.netty;

import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.api.traffic.TrafficData;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.protocol.AbstractPacketHandler;

public class ProtocolizeOutboundTrafficHandler extends MessageToByteEncoder<ByteBuf> {

    private final Connection connection;
    private final Stream stream;

    public ProtocolizeOutboundTrafficHandler(final AbstractPacketHandler abstractPacketHandler, final Stream stream) {
        connection = ReflectionUtil.getConnection(abstractPacketHandler, ReflectionUtil.serverConnectorClass.isInstance(abstractPacketHandler));
        this.stream = stream;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) throws Exception {
        final TrafficData data = ProtocolAPI.getTrafficManager().getData(ReflectionUtil.getConnectionName(connection), connection);
        if(stream == Stream.UPSTREAM) {
            data.setUpstreamOutputCurrentMinute(data.getUpstreamOutputCurrentMinute()+msg.readableBytes());
            data.setUpstreamOutput(data.getUpstreamOutput()+msg.readableBytes());
        } else {
            data.setDownstreamOutputCurrentMinute(data.getDownstreamOutputCurrentMinute()+msg.readableBytes());
            data.setDownstreamOutput(data.getDownstreamOutput()+msg.readableBytes());
            data.setDownstreamBridgeName(ReflectionUtil.getServerName(connection));
        }
        out.writeBytes(msg);
    }

}
