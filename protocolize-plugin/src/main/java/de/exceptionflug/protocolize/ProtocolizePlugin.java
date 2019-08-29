package de.exceptionflug.protocolize;

import com.google.common.io.ByteStreams;
import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import de.exceptionflug.protocolize.command.ProtocolizeCommand;
import de.exceptionflug.protocolize.command.ProxyInvCommand;
import de.exceptionflug.protocolize.command.TrafficCommand;
import de.exceptionflug.protocolize.injector.NettyPipelineInjector;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.items.ItemsModule;
import de.exceptionflug.protocolize.listener.PlayerListener;
import de.exceptionflug.protocolize.netty.ProtocolizeDecoderChannelHandler;
import de.exceptionflug.protocolize.world.WorldModule;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ProtocolizePlugin extends Plugin {

    private final NettyPipelineInjector nettyPipelineInjector = new NettyPipelineInjector();
    private boolean enabled = true;
    private boolean overwrittenInitializer;

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getLogger().info("======= PROTOCOLIZE =======");
        ProxyServer.getInstance().getLogger().info("Version " + getDescription().getVersion() + " by " + getDescription().getAuthor());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener(this));

        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
            final File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                file.createNewFile();
                try (final InputStream is = getResourceAsStream("config.yml");
                     final OutputStream os = new FileOutputStream(file)) {
                    ByteStreams.copy(is, os);
                }
            }
            final Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            InventoryModule.setSpigotInventoryTracking(configuration.getBoolean("experimental.spigot-gui-inventory-tracking"));
            ItemsModule.setSpigotInventoryTracking(configuration.getBoolean("experimental.spigot-player-inventory-tracking"));
            ProtocolAPI.getEventManager().setFireBungeeEvent(configuration.getBoolean("fireBungeeEvents"));
        } catch (final IOException e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Failed to load config", e);
        }


        // Init system components
        WorldModule.initModule();
        ItemsModule.initModule();
        InventoryModule.initModule();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ProxyInvCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ProtocolizeCommand(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TrafficCommand());

        // Try register modified channel initializer
        try {
            overwriteInitializer();
            overwrittenInitializer = true;
        } catch (Exception e) {
            ProxyServer.getInstance().getLogger().warning("[Protocolize] Overwriting the channel initializer is not working. Falling back to default injection.");
        }
    }

    private void overwriteInitializer() throws Exception {
        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                if (BungeeCord.getInstance().getConnectionThrottle() != null && BungeeCord.getInstance().getConnectionThrottle().throttle(((InetSocketAddress) ch.remoteAddress()).getAddress())) {
                    ch.close();
                    return;
                }

                ListenerInfo listener = ch.attr(PipelineUtils.LISTENER).get();

                PipelineUtils.BASE.initChannel(ch);
                InitialHandler handler = new InitialHandler(BungeeCord.getInstance(), listener);
                ch.pipeline().addBefore("inbound-boss", "protocolize-decoder",  new ProtocolizeDecoderChannelHandler(handler, Stream.UPSTREAM));
                ch.pipeline().addBefore(PipelineUtils.FRAME_DECODER, PipelineUtils.LEGACY_DECODER, new LegacyDecoder());
                ch.pipeline().addAfter(PipelineUtils.FRAME_DECODER, PipelineUtils.PACKET_DECODER, new MinecraftDecoder(Protocol.HANDSHAKE, true, ProxyServer.getInstance().getProtocolVersion()));
                ch.pipeline().addAfter(PipelineUtils.FRAME_PREPENDER, PipelineUtils.PACKET_ENCODER, new MinecraftEncoder(Protocol.HANDSHAKE, true, ProxyServer.getInstance().getProtocolVersion()));
                ch.pipeline().addBefore(PipelineUtils.FRAME_PREPENDER, PipelineUtils.LEGACY_KICKER, ReflectionUtil.getKickStringWriter());
                ch.pipeline().get(HandlerBoss.class).setHandler(handler);

                if (listener.isProxyProtocol()) {
                    ch.pipeline().addFirst(new HAProxyMessageDecoder());
                }
            }
        };

        Field initField = PipelineUtils.class.getField("SERVER_CHILD");
        Field modifierField = Field.class.getDeclaredField("modifiers");
        modifierField.setAccessible(true);
        modifierField.set(initField, initField.getModifiers() & ~Modifier.FINAL);
        initField.setAccessible(true);
        initField.set(null, initializer);
    }

    public NettyPipelineInjector getNettyPipelineInjector() {
        return nettyPipelineInjector;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isOverwrittenInitializer() {
        return overwrittenInitializer;
    }

    public static boolean isExceptionCausedByProtocolize(final Throwable e) {
        final List<StackTraceElement> all = getEverything(e, new ArrayList<>());
        for (final StackTraceElement element : all) {
            if (element.getClassName().toLowerCase().contains("de.exceptionflug"))
                return true;
        }
        return false;
    }

    private static List<StackTraceElement> getEverything(final Throwable e, List<StackTraceElement> objects) {
        if (e.getCause() != null)
            objects = getEverything(e.getCause(), objects);
        objects.addAll(Arrays.asList(e.getStackTrace()));
        return objects;
    }

}
