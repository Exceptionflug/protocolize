package dev.simplix.protocolize.velocity;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.network.ConnectionManager;
import dev.simplix.protocolize.api.PlatformInitializer;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.*;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.velocity.commands.ProtocolizeCommand;
import dev.simplix.protocolize.velocity.listener.PlayerListener;
import dev.simplix.protocolize.velocity.netty.ProtocolizeBackendChannelInitializer;
import dev.simplix.protocolize.velocity.netty.ProtocolizeServerChannelInitializer;
import dev.simplix.protocolize.velocity.providers.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 22.08.2021
 *
 * @author Exceptionflug
 */
@Plugin(name = "Protocolize", authors = "Exceptionflug", version = "v2", id = "protocolize")
public class ProtocolizePlugin {

    private static String version = readVersion();

    static {
        PlatformInitializer.initVelocity(version());
    }

    private final ProxyServer proxyServer;
    private final Logger logger;
    private boolean supported;

    @Inject
    public ProtocolizePlugin(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        initProviders();
    }

    public static String version() {
        return version;
    }

    public static boolean isExceptionCausedByProtocolize(Throwable cause) {
        final List<StackTraceElement> all = getEverything(cause, new ArrayList<>());
        for (final StackTraceElement element : all) {
            if (element.getClassName().toLowerCase().contains("dev.simplix")
                && !element.getClassName().contains("dev.simplix.protocolize.bungee.netty.ProtocolizeEncoderChannelHandler.exceptionCaught"))
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

    private void initProviders() {
        Protocolize.registerService(ComponentConverterProvider.class, new VelocityComponentConverterProvider());
        Protocolize.registerService(ProtocolizePlayerProvider.class, new VelocityProtocolizePlayerProvider(proxyServer));
        Protocolize.registerService(ModuleProvider.class, new VelocityModuleProvider());
        Protocolize.registerService(ProtocolRegistrationProvider.class, new VelocityProtocolRegistrationProvider());
        Protocolize.registerService(PacketListenerProvider.class, new VelocityPacketListenerProvider());
    }

    private static String readVersion() {
        try (InputStream inputStream = ProtocolizePlugin.class.getResourceAsStream("/version.txt")) {
            return new String(ByteStreams.toByteArray(inputStream), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LoggerFactory.getLogger("Protocolize").warn("Unable to read version", e);
        }
        return "2.?.?:unknown";
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) throws ReflectiveOperationException {
        logger.info("======= PROTOCOLIZE =======");
        logger.info("Version " + version + " by " + description().getAuthors().toString().replace("[", "").replace("]", ""));
        if (version.endsWith(":unknown")) {
            logger.warn("WARNING: YOU ARE RUNNING AN UNOFFICIAL BUILD OF PROTOCOLIZE. DON'T REPORT ANY BUGS REGARDING THIS VERSION.");
        }
        logger.info("Swap channel initializers (ignore the following two warnings)...");
        swapChannelInitializers();
        logger.info("Swapped channel initializers");

        proxyServer.getCommandManager().register("protocolize", new ProtocolizeCommand(this));
        proxyServer.getEventManager().register(this, new PlayerListener(this));

        ((VelocityModuleProvider) Protocolize.getService(ModuleProvider.class)).enableAll();

        Protocolize.playerProvider().onConstruct(protocolizePlayer -> {
            if (Protocolize.getService(ModuleProvider.class).moduleInstalled("LegacyModule")) {
                return;
            }
            Player player = protocolizePlayer.handle();
            if (protocolizePlayer.protocolVersion() < ProtocolVersions.MINECRAFT_1_13) {
                logger.warn("=== WARNING ===");
                logger.warn("The player " + player.getUsername() + " is using "
                    + player.getProtocolVersion().getMostRecentSupportedVersion() + " which is not supported by protocolize by default.");
                logger.warn("You may experience log spamming due to protocolize not finding appropriate mappings for the clients protocol version.");
                logger.warn("To fix this you have to install the legacy support module for velocity. More info at: https://simplixsoft.com/protocolize");
            }
        });
    }

    private void swapChannelInitializers() throws ReflectiveOperationException {
        Field cm = VelocityServer.class.getDeclaredField("cm");
        cm.setAccessible(true);
        ConnectionManager connectionManager = (ConnectionManager) cm.get(proxyServer);
        connectionManager.getBackendChannelInitializer().set(new ProtocolizeBackendChannelInitializer((VelocityServer) proxyServer,
            connectionManager.getBackendChannelInitializer().get()));
        connectionManager.getServerChannelInitializer().set(new ProtocolizeServerChannelInitializer((VelocityServer) proxyServer,
            connectionManager.getServerChannelInitializer().get()));
    }

    public ChannelInitializer<Channel> currentBackendChannelInitializer() throws ReflectiveOperationException {
        Field cm = VelocityServer.class.getDeclaredField("cm");
        cm.setAccessible(true);
        ConnectionManager connectionManager = (ConnectionManager) cm.get(proxyServer);
        return connectionManager.getBackendChannelInitializer().get();
    }

    public PluginDescription description() {
        return proxyServer.getPluginManager().getPlugin("protocolize").get().getDescription();
    }

}
