package dev.simplix.protocolize.velocity.netty;

import com.velocitypowered.api.proxy.InboundConnection;
import io.netty.channel.ChannelPipeline;

public interface PipelineAccessor {

    ChannelPipeline get(InboundConnection connection);

}
