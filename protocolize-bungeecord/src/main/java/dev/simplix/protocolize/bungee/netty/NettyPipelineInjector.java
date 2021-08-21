package dev.simplix.protocolize.bungee.netty;

import com.google.common.base.Preconditions;
import dev.simplix.protocolize.bungee.util.ReflectionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;

import java.util.logging.Level;

public final class NettyPipelineInjector {

    public void injectBefore(Connection connection, String baseName, String name, ChannelHandler channelHandler) {
        Preconditions.checkNotNull(connection, "The connection cannot be null!");
        Preconditions.checkNotNull(name, "The name cannot be null!");
        Preconditions.checkNotNull(baseName, "The baseName cannot be null!");
        Preconditions.checkNotNull(channelHandler, "The channelHandler cannot be null!");
        try {
            final Object channelWrapper = ReflectionUtil.getChannelWrapper(connection);
            if (channelWrapper != null)
                ((Channel) ReflectionUtil.channelWrapperChannelField.get(channelWrapper)).pipeline().addBefore(baseName, name, channelHandler);
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception occurred while injecting into netty pipeline", e);
        }
    }

    public void injectAfter(Connection connection, String baseName, String name, ChannelHandler channelHandler) {
        Preconditions.checkNotNull(connection, "The connection cannot be null!");
        Preconditions.checkNotNull(name, "The name cannot be null!");
        Preconditions.checkNotNull(baseName, "The baseName cannot be null!");
        Preconditions.checkNotNull(channelHandler, "The channelHandler cannot be null!");
        try {
            final Object channelWrapper = ReflectionUtil.getChannelWrapper(connection);
            if (channelWrapper != null)
                ((Channel) ReflectionUtil.channelWrapperChannelField.get(channelWrapper)).pipeline().addAfter(baseName, name, channelHandler);
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception occurred while injecting into netty pipeline", e);
        }
    }

}
