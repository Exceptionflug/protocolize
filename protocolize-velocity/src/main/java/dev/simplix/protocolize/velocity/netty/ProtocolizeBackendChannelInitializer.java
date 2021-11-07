package dev.simplix.protocolize.velocity.netty;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.network.BackendChannelInitializer;
import dev.simplix.protocolize.api.Direction;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Date: 22.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public class ProtocolizeBackendChannelInitializer extends BackendChannelInitializer {

    private final ChannelInitializer<Channel> wrapped;
    private Method initMethod;

    public ProtocolizeBackendChannelInitializer(VelocityServer server, ChannelInitializer<Channel> wrapped) {
        super(server);
        this.wrapped = wrapped;

        try {
            initMethod = wrapped.getClass().getDeclaredMethod("initChannel", Channel.class);
            initMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            log.error("Unsupported backend channel initializer: " + wrapped.getClass().getName(), e);
        }
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        if (wrapped != null && initMethod != null) {
            initMethod.invoke(wrapped, ch);
        }
        if (!ch.pipeline().toMap().containsKey("frame-decoder")) {
            super.initChannel(ch);
        }
        ch.pipeline().addLast("protocolize2-decoder", new ProtocolizeDecoderChannelHandler(Direction.DOWNSTREAM));
        ch.pipeline().addLast("protocolize2-encoder", new ProtocolizeEncoderChannelHandler(Direction.DOWNSTREAM));
    }
}
