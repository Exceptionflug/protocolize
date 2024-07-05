package dev.simplix.protocolize.velocity.netty;

import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.network.Connections;
import com.velocitypowered.proxy.network.ServerChannelInitializer;
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
@Slf4j(topic = "Protocolize")
public class ProtocolizeServerChannelInitializer extends ServerChannelInitializer {

    private final ChannelInitializer<Channel> wrapped;
    private Method initMethod;

    public ProtocolizeServerChannelInitializer(VelocityServer server, ChannelInitializer<Channel> wrapped) {
        super(server);
        this.wrapped = wrapped;

        if (wrapped != null) {
            log.info("Respecting the previous registered ServerChannelInitializer: {}", wrapped.getClass().getName());
            try {
                initMethod = this.wrapped.getClass().getDeclaredMethod("initChannel", Channel.class);
                initMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                log.error("Unsupported server channel initializer: {}", this.wrapped.getClass().getName(), e);
            }
        }
    }

    @Override
    protected void initChannel(Channel ch) {
        try {
            if (wrapped != null && initMethod != null) {
                log.debug("Calling the underlying server channel initializer: {}", wrapped.getClass().getName());
                initMethod.invoke(wrapped, ch);
            }
        } catch (Exception e) {
            log.error("There was a problem while calling the underlying server channel initializer", e);
        } finally {
            if (!ch.pipeline().toMap().containsKey("frame-decoder")) {
                log.debug("Initialize vanilla pipeline handlers for upstream");
                super.initChannel(ch);
            }
            ch.pipeline().addBefore(Connections.HANDLER, "protocolize2-decoder", new ProtocolizeDecoderChannelHandler(Direction.UPSTREAM));
            ch.pipeline().addLast("protocolize2-encoder", new ProtocolizeEncoderChannelHandler(Direction.UPSTREAM));
        }
    }
}
