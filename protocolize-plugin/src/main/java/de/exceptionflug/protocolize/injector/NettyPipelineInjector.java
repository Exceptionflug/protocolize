package de.exceptionflug.protocolize.injector;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.lang.reflect.Field;
import java.util.logging.Level;

public final class NettyPipelineInjector {

    private Field userConnectionChannelWrapperField, serverConnectionChannelWrapperField, initialHandlerChannelWrapperField, channelWrapperChannelField;

    {
        try {
            final Class userConnectionClass = Class.forName("net.md_5.bungee.UserConnection");
            userConnectionChannelWrapperField = userConnectionClass.getDeclaredField("ch");
            userConnectionChannelWrapperField.setAccessible(true);
            final Class serverConnectionClass = Class.forName("net.md_5.bungee.ServerConnection");
            serverConnectionChannelWrapperField = serverConnectionClass.getDeclaredField("ch");
            serverConnectionChannelWrapperField.setAccessible(true);
            final Class initialHandlerClass = Class.forName("net.md_5.bungee.connection.InitialHandler");
            initialHandlerChannelWrapperField = initialHandlerClass.getDeclaredField("ch");
            initialHandlerChannelWrapperField.setAccessible(true);
            final Class channelWrapperClass = Class.forName("net.md_5.bungee.netty.ChannelWrapper");
            channelWrapperChannelField = channelWrapperClass.getDeclaredField("ch");
            channelWrapperChannelField.setAccessible(true);
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception while initializing NettyPipelineInjector: ", e);
        }
    }

    public void injectBefore(final Connection connection, final String baseName, final String name, final ChannelHandler channelHandler) {
        Preconditions.checkNotNull(connection, "The connection cannot be null!");
        Preconditions.checkNotNull(name, "The name cannot be null!");
        Preconditions.checkNotNull(baseName, "The baseName cannot be null!");
        Preconditions.checkNotNull(channelHandler, "The channelHandler cannot be null!");
        try {
            final Object channelWrapper = getChannelWrapper(connection);
            if(channelWrapper != null)
                ((Channel)channelWrapperChannelField.get(channelWrapper)).pipeline().addBefore(baseName, name, channelHandler);
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception occurred while injecting into netty pipeline", e);
        }
    }

    public void injectAfter(final Connection connection, final String baseName, final String name, final ChannelHandler channelHandler) {
        Preconditions.checkNotNull(connection, "The connection cannot be null!");
        Preconditions.checkNotNull(name, "The name cannot be null!");
        Preconditions.checkNotNull(baseName, "The baseName cannot be null!");
        Preconditions.checkNotNull(channelHandler, "The channelHandler cannot be null!");
        try {
            final Object channelWrapper = getChannelWrapper(connection);
            if(channelWrapper != null)
                ((Channel)channelWrapperChannelField.get(channelWrapper)).pipeline().addAfter(baseName, name, channelHandler);
        } catch (final Exception e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception occurred while injecting into netty pipeline", e);
        }
    }

    private Object getChannelWrapper(final Connection connection) throws IllegalAccessException {
        final Object channelWrapper;
        if(connection instanceof ProxiedPlayer) {
            channelWrapper = userConnectionChannelWrapperField.get(connection);
        } else if(connection instanceof Server) {
            channelWrapper = serverConnectionChannelWrapperField.get(connection);
        } else if(connection instanceof PendingConnection) {
            channelWrapper = initialHandlerChannelWrapperField.get(connection);
        } else {
            channelWrapper = null;
        }
        return channelWrapper;
    }
}
