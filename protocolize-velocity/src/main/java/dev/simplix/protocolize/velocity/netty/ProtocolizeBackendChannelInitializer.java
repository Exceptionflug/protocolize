package dev.simplix.protocolize.velocity.netty;

import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.network.BackendChannelInitializer;
import dev.simplix.protocolize.api.Direction;
import io.netty.channel.Channel;

/**
 * Date: 22.08.2021
 *
 * @author Exceptionflug
 */
public class ProtocolizeBackendChannelInitializer extends BackendChannelInitializer {

    public ProtocolizeBackendChannelInitializer(VelocityServer server) {
        super(server);
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        super.initChannel(ch);
        ch.pipeline().addLast("protocolize2-decoder", new ProtocolizeDecoderChannelHandler(Direction.DOWNSTREAM));
    }
}
