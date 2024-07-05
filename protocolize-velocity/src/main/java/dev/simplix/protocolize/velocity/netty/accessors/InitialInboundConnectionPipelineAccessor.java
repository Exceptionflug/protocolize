package dev.simplix.protocolize.velocity.netty.accessors;

import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.proxy.connection.MinecraftConnection;
import com.velocitypowered.proxy.connection.client.InitialInboundConnection;
import dev.simplix.protocolize.api.util.ReflectionUtil;
import dev.simplix.protocolize.velocity.netty.PipelineAccessor;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public final class InitialInboundConnectionPipelineAccessor implements PipelineAccessor {

    private static final Field MINECRAFT_CONNECTION_FIELD = ReflectionUtil.fieldOrNull(InitialInboundConnection.class, "connection", true);

    @Override
    public ChannelPipeline get(InboundConnection connection) {
        try {
            return ((MinecraftConnection) MINECRAFT_CONNECTION_FIELD.get(connection)).getChannel().pipeline();
        } catch (Exception e) {
            log.error("Unable to get pipeline from {}: ", connection, e);
            return null;
        }
    }

}
