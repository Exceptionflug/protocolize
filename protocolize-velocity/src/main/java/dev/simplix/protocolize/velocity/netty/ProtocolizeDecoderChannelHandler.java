package dev.simplix.protocolize.velocity.netty;

import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.packet.Chat;
import dev.simplix.protocolize.api.Direction;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public final class ProtocolizeDecoderChannelHandler extends MessageToMessageDecoder<MinecraftPacket> {

    private final Direction direction;

    public ProtocolizeDecoderChannelHandler(Direction direction) {
        this.direction = direction;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, MinecraftPacket minecraftPacket, List<Object> list) throws Exception {
        if (minecraftPacket != null) {
            try {
                if (minecraftPacket instanceof Chat) {
                    System.out.println(direction.name()+": "+((Chat) minecraftPacket).getMessage());
                }
            } finally {
                ReferenceCountUtil.retain(minecraftPacket);
                list.add(minecraftPacket);
            }
        }
    }

}