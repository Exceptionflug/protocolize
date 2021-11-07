package dev.simplix.protocolize.velocity.netty;

import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.network.Connections;
import com.velocitypowered.proxy.network.ServerChannelInitializer;
import dev.simplix.protocolize.api.Direction;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Date: 22.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public class ProtocolizeServerChannelInitializer extends ServerChannelInitializer {

    private final ChannelInitializer<Channel> wrapped;
    private Method initMethod;

    public ProtocolizeServerChannelInitializer(VelocityServer server, ChannelInitializer<Channel> wrapped) {
        super(server);
        this.wrapped = wrapped;

        try {
            initMethod = this.wrapped.getClass().getDeclaredMethod("initChannel", Channel.class);
            initMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            log.error("Unsupported backend channel initializer: " + this.wrapped.getClass().getName(), e);
        }
    }

    @SneakyThrows
    @Override
    protected void initChannel(Channel ch) {
        if (wrapped != null && initMethod != null) {
            initMethod.invoke(wrapped, ch);
        }
        if (!ch.pipeline().toMap().containsKey("frame-decoder")) {
            super.initChannel(ch);
        }
        ch.pipeline().addBefore(Connections.HANDLER, "protocolize2-decoder", new ProtocolizeDecoderChannelHandler(Direction.UPSTREAM));
        ch.pipeline().addLast("protocolize2-encoder", new ProtocolizeEncoderChannelHandler(Direction.UPSTREAM));
    }
}
