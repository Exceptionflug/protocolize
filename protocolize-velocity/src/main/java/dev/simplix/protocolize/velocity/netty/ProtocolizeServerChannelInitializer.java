package dev.simplix.protocolize.velocity.netty;

import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.network.Connections;
import com.velocitypowered.proxy.network.ServerChannelInitializer;
import dev.simplix.protocolize.api.Direction;
import io.netty.channel.Channel;

/**
 * Date: 22.08.2021
 *
 * @author Exceptionflug
 */
public class ProtocolizeServerChannelInitializer extends ServerChannelInitializer {

    public ProtocolizeServerChannelInitializer(VelocityServer server) {
        super(server);
    }

    @Override
    protected void initChannel(Channel ch) {
        super.initChannel(ch);
        ch.pipeline().addBefore(Connections.HANDLER, "protocolize2-decoder", new ProtocolizeDecoderChannelHandler(Direction.UPSTREAM));
        ch.pipeline().addLast("protocolize2-encoder", new ProtocolizeEncoderChannelHandler(Direction.UPSTREAM));
    }
}
