package dev.simplix.protocolize.bungee;

import dev.simplix.protocolize.api.PlatformInitializer;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import dev.simplix.protocolize.api.providers.ModuleProvider;
import dev.simplix.protocolize.api.providers.PacketListenerProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.bungee.commands.ProtocolizeCommand;
import dev.simplix.protocolize.bungee.listener.PlayerListener;
import dev.simplix.protocolize.bungee.netty.NettyPipelineInjector;
import dev.simplix.protocolize.bungee.providers.BungeeCordComponentConverterProvider;
import dev.simplix.protocolize.bungee.providers.BungeeCordModuleProvider;
import dev.simplix.protocolize.bungee.providers.BungeeCordPacketListenerProvider;
import dev.simplix.protocolize.bungee.providers.BungeeCordProtocolRegistrationProvider;
import dev.simplix.protocolize.bungee.strategies.AegisPacketRegistrationStrategy;
import dev.simplix.protocolize.bungee.strategies.BungeeCordPacketRegistrationStrategy;
import dev.simplix.protocolize.bungee.strategies.LegacyBungeeCordPacketRegistrationStrategy;
import dev.simplix.protocolize.bungee.strategy.PacketRegistrationStrategy;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
public class ProtocolizePlugin extends Plugin {

    private final NettyPipelineInjector pipelineInjector = new NettyPipelineInjector();
    private BungeeCordProtocolRegistrationProvider registrationProvider;
    private boolean supported;

    static {
        PlatformInitializer.initBungeeCord();
    }

    @Override
    public void onLoad() {
        List<PacketRegistrationStrategy> strategies = new ArrayList<>();
        strategies.add(new BungeeCordPacketRegistrationStrategy());
        strategies.add(new LegacyBungeeCordPacketRegistrationStrategy());
        strategies.add(new AegisPacketRegistrationStrategy());

        try {
            registrationProvider = new BungeeCordProtocolRegistrationProvider(strategies);
            supported = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        Protocolize.registerService(ComponentConverterProvider.class, new BungeeCordComponentConverterProvider());
        Protocolize.registerService(ModuleProvider.class, new BungeeCordModuleProvider());
        Protocolize.registerService(ProtocolRegistrationProvider.class, registrationProvider);
        Protocolize.registerService(PacketListenerProvider.class, new BungeeCordPacketListenerProvider());
    }

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getLogger().info("======= PROTOCOLIZE =======");
        ProxyServer.getInstance().getLogger().info("Version " + getDescription().getVersion() + " by " + getDescription().getAuthor());
        ProxyServer.getInstance().getLogger().info("Supported: " + (supported ? "Yes ("+registrationProvider.strategy().getClass().getSimpleName() + ")" : "No"));
        if (getDescription().getVersion().endsWith(":unknown")) {
            ProxyServer.getInstance().getLogger().warning("WARNING: YOU ARE RUNNING AN UNOFFICIAL BUILD OF PROTOCOLIZE. DON'T REPORT ANY BUGS REGARDING THIS VERSION.");
        }

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ProtocolizeCommand(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener(this));

        ((BungeeCordModuleProvider)Protocolize.getService(ModuleProvider.class)).enableAll();
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

    public NettyPipelineInjector nettyPipelineInjector() {
        return pipelineInjector;
    }
}
