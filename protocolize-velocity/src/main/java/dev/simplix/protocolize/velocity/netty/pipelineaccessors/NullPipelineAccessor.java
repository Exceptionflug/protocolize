package dev.simplix.protocolize.velocity.netty.pipelineaccessors;

import com.velocitypowered.api.proxy.InboundConnection;
import dev.simplix.protocolize.velocity.netty.PipelineAccessor;
import io.netty.channel.ChannelPipeline;

public final class NullPipelineAccessor implements PipelineAccessor {

    @Override
    public ChannelPipeline get(InboundConnection connection) {
        return null;
    }

}
