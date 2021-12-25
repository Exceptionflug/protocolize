package dev.simplix.protocolize.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.connection.client.InitialInboundConnection;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.util.ReflectionUtil;
import dev.simplix.protocolize.velocity.ProtocolizePlugin;
import dev.simplix.protocolize.velocity.netty.PipelineAccessor;
import dev.simplix.protocolize.velocity.netty.ProtocolizeBackendChannelInitializer;
import dev.simplix.protocolize.velocity.netty.ProtocolizeDecoderChannelHandler;
import dev.simplix.protocolize.velocity.netty.ProtocolizeEncoderChannelHandler;
import dev.simplix.protocolize.velocity.netty.accessors.ConnectedPlayerPipelineAccessor;
import dev.simplix.protocolize.velocity.netty.accessors.InitialInboundConnectionPipelineAccessor;
import dev.simplix.protocolize.velocity.netty.accessors.NullPipelineAccessor;
import dev.simplix.protocolize.velocity.providers.VelocityProtocolizePlayerProvider;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public class PlayerListener {

    private static final Map<String, PipelineAccessor> PIPELINE_ACCESSOR_MAP = new HashMap<>();
    private static final VelocityProtocolizePlayerProvider PLAYER_PROVIDER = (VelocityProtocolizePlayerProvider) Protocolize.playerProvider();
    private static final Field SERVER_CONNECTION_FIELD = ReflectionUtil.fieldOrNull(ConnectedPlayer.class, "connectionInFlight", true);

    private final ProtocolizePlugin plugin;

    static {
        PIPELINE_ACCESSOR_MAP.put(InitialInboundConnection.class.getName(), new InitialInboundConnectionPipelineAccessor());
        PIPELINE_ACCESSOR_MAP.put(ConnectedPlayer.class.getName(), new ConnectedPlayerPipelineAccessor());
        PIPELINE_ACCESSOR_MAP.put("com.velocitypowered.proxy.connection.client.HandshakeSessionHandler$LegacyInboundConnection", new NullPipelineAccessor());
        PIPELINE_ACCESSOR_MAP.put("org.geysermc.platform.velocity.GeyserVelocityPingPassthrough$GeyserInboundConnection", new NullPipelineAccessor());
    }

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
        PipelineAccessor accessor = PIPELINE_ACCESSOR_MAP.get(connection.getClass().getName());
        if (accessor == null) {
            throw new UnsupportedOperationException("InboundConnection of type " + connection.getClass().getName()
                + " is not supported. Protocolize will not work properly. Please check for incompatible plugins.");
        }
        ChannelPipeline pipeline = accessor.get(connection);
        if (pipeline == null) {
            return;
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
                // WTF??
                log.error("Pipeline is not initialized. This is a bug. Please report. Pipeline handlers = " + pipeline.toMap());
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
