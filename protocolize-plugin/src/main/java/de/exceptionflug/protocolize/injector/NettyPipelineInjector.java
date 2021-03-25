package de.exceptionflug.protocolize.injector;

import com.google.common.base.Preconditions;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import net.md_5.bungee.api.connection.Connection;

public final class NettyPipelineInjector {

    public void injectBefore(final Connection connection, final String baseName, final String name, final ChannelHandler channelHandler) {
        Preconditions.checkNotNull(connection, "The connection cannot be null!");
        Preconditions.checkNotNull(name, "The name cannot be null!");
        Preconditions.checkNotNull(baseName, "The baseName cannot be null!");
        Preconditions.checkNotNull(channelHandler, "The channelHandler cannot be null!");
        try {
            final Object channelWrapper = ReflectionUtil.getChannelWrapper(connection);
            if (channelWrapper != null) {
                if (((Channel) ReflectionUtil.channelWrapperChannelField.get(channelWrapper)).pipeline().get(name) == null) {
                    ((Channel) ReflectionUtil.channelWrapperChannelField.get(channelWrapper)).pipeline().addBefore(baseName, name, channelHandler);
                } else {
                    ((Channel) ReflectionUtil.channelWrapperChannelField.get(channelWrapper)).pipeline().replace(baseName, name, channelHandler);
                }
            }
        } catch (final Exception e) {
//            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception occurred while injecting into netty pipeline", e);
        }
    }

    public void injectAfter(final Connection connection, final String baseName, final String name, final ChannelHandler channelHandler) {
        Preconditions.checkNotNull(connection, "The connection cannot be null!");
        Preconditions.checkNotNull(name, "The name cannot be null!");
        Preconditions.checkNotNull(baseName, "The baseName cannot be null!");
        Preconditions.checkNotNull(channelHandler, "The channelHandler cannot be null!");
        try {
            final Object channelWrapper = ReflectionUtil.getChannelWrapper(connection);
            if (channelWrapper != null) {
                if (((Channel) ReflectionUtil.channelWrapperChannelField.get(channelWrapper)).pipeline().get(name) == null) {
                    ((Channel) ReflectionUtil.channelWrapperChannelField.get(channelWrapper)).pipeline().addAfter(baseName, name, channelHandler);
                } else {
                    ((Channel) ReflectionUtil.channelWrapperChannelField.get(channelWrapper)).pipeline().replace(baseName, name, channelHandler);
                }
            }
        } catch (final Exception e) {
//            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception occurred while injecting into netty pipeline", e);
        }
    }

}
