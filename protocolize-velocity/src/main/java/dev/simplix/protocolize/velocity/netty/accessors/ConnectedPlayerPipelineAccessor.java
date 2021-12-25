package dev.simplix.protocolize.velocity.netty.accessors;

import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import dev.simplix.protocolize.velocity.netty.PipelineAccessor;
import io.netty.channel.ChannelPipeline;

public final class ConnectedPlayerPipelineAccessor implements PipelineAccessor {

    @Override
    public ChannelPipeline get(InboundConnection connection) {
        return ((ConnectedPlayer) connection).getConnection().getChannel().pipeline();
    }

}
