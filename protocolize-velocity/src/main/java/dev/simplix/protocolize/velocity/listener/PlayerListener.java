package dev.simplix.protocolize.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.proxy.connection.MinecraftConnection;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.connection.client.InitialInboundConnection;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.util.ReflectionUtil;
import dev.simplix.protocolize.velocity.ProtocolizePlugin;
import dev.simplix.protocolize.velocity.netty.ProtocolizeBackendChannelInitializer;
import dev.simplix.protocolize.velocity.netty.ProtocolizeDecoderChannelHandler;
import dev.simplix.protocolize.velocity.netty.ProtocolizeEncoderChannelHandler;
import dev.simplix.protocolize.velocity.providers.VelocityProtocolizePlayerProvider;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public class PlayerListener {

    private static final VelocityProtocolizePlayerProvider PLAYER_PROVIDER = (VelocityProtocolizePlayerProvider) Protocolize.playerProvider();
    private static final Field SERVER_CONNECTION_FIELD = ReflectionUtil.fieldOrNull(ConnectedPlayer.class, "connectionInFlight", true);
    private static final Field MINECRAFT_CONNECTION_FIELD = ReflectionUtil.fieldOrNull(InitialInboundConnection.class, "connection", true);

    private final ProtocolizePlugin plugin;

    public PlayerListener(ProtocolizePlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        try {
            initConnection(event.getConnection());
        } catch (Exception e) {
            log.error("Unable to initialize InboundConnection on ping", e);
        }
    }

    @Subscribe
    public void onPreLogin(LoginEvent event) {
        try {
            initConnection(event.getPlayer());
        } catch (Exception e) {
            log.error("Unable to initialize InboundConnection on pre login", e);
        }
    }

    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        try {
            initConnection((ServerConnection) SERVER_CONNECTION_FIELD.get(event.getPlayer()));
        } catch (Exception e) {
            log.error("Unable to initialize InboundConnection on server switch", e);
        }
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        PLAYER_PROVIDER.playerDisconnect(event.getPlayer().getUniqueId());
    }

    private void initConnection(InboundConnection connection) throws ReflectiveOperationException {
        ChannelPipeline pipeline;
        if (connection instanceof InitialInboundConnection) {
            pipeline = ((MinecraftConnection) MINECRAFT_CONNECTION_FIELD.get(connection)).getChannel().pipeline();
        } else if (connection instanceof ConnectedPlayer) {
            pipeline = ((ConnectedPlayer) connection).getConnection().getChannel().pipeline();
        } else if (connection.getClass().getName().equals("com.velocitypowered.proxy.connection.client.HandshakeSessionHandler$LegacyInboundConnection")) {
            return; // Allow it but we can't give support for that
        } else {
            throw new IllegalArgumentException("Unsupported InboundConnection instance: " + connection.getClass().getName());
        }
        ProtocolizeDecoderChannelHandler decoderChannelHandler = pipeline.get(ProtocolizeDecoderChannelHandler.class);
        if (decoderChannelHandler == null) {
            // Ah yes expecting an overridden channel initializer
            ChannelInitializer<Channel> initializer = plugin.currentBackendChannelInitializer();
            if (initializer.getClass() != ProtocolizeBackendChannelInitializer.class) {
                log.error("It seems like there is an incompatible plugin installed. Velocity channel initializers are overridden by: "
                    + initializer.getClass().getName());
                log.error("Protocolize is unable to work under this circumstances. Please contact the developers of the incompatible " +
                    "plugin and suggest them to call the initChannel method of the ChannelInitializers before overriding them.");
                return;
            } else {
                // ?!?!?
                throw new IllegalStateException("Missing ProtocolizeDecoderChannelHandler in pipeline. Maybe there is some incompatible plugin installed?");
            }
        }
        decoderChannelHandler.connection(connection);
        pipeline.get(ProtocolizeEncoderChannelHandler.class).connection(connection);
    }

    private void initConnection(ServerConnection connection) {
        ChannelPipeline pipeline;
        if (connection instanceof VelocityServerConnection) {
            pipeline = ((VelocityServerConnection) connection).getConnection().getChannel().pipeline();
        } else {
            throw new IllegalArgumentException("Unsupported InboundConnection instance: " + connection.getClass().getName());
        }
        pipeline.get(ProtocolizeDecoderChannelHandler.class).connection(connection);
        pipeline.get(ProtocolizeEncoderChannelHandler.class).connection(connection);
    }

}
