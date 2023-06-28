package dev.simplix.protocolize.velocity.netty;

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
    private static final Method INIT_METHOD;

    static {
        try {
            INIT_METHOD = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
            INIT_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unsupported velocity version. Please use a different version.", e);
        }
    }

    public ProtocolizeBackendChannelInitializer(VelocityServer server, ChannelInitializer<Channel> wrapped) {
        super(server);
        this.wrapped = wrapped;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        try {
            if (wrapped != null) {
                log.debug("Calling the underlying backend channel initializer: " + wrapped.getClass().getName());
                INIT_METHOD.invoke(wrapped, ch);
            }
        } catch (Exception e) {
            log.error("There was a problem while calling the underlying backend channel initializer", e);
        } finally {
            if (!ch.pipeline().toMap().containsKey("frame-decoder")) {
                log.debug("Initialize vanilla pipeline handlers for downstream");
                super.initChannel(ch);
            }
            ch.pipeline().addLast("protocolize2-decoder", new ProtocolizeDecoderChannelHandler(Direction.DOWNSTREAM));
            ch.pipeline().addLast("protocolize2-encoder", new ProtocolizeEncoderChannelHandler(Direction.DOWNSTREAM));
        }
    }
}
