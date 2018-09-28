package de.exceptionflug.protocolize;

import de.exceptionflug.protocolize.command.ProtocolizeCommand;
import de.exceptionflug.protocolize.command.ProxyInvCommand;
import de.exceptionflug.protocolize.command.TrafficCommand;
import de.exceptionflug.protocolize.injector.NettyPipelineInjector;
import de.exceptionflug.protocolize.items.ItemsModule;
import de.exceptionflug.protocolize.listener.PlayerListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class ProtocolizePlugin extends Plugin {

    private final NettyPipelineInjector nettyPipelineInjector = new NettyPipelineInjector();

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getLogger().info("======= PROTOCOLIZE =======");
        ProxyServer.getInstance().getLogger().info("Version "+getDescription().getVersion()+" by "+getDescription().getAuthor());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener(this));

        // Init system components
        ItemsModule.initModule();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ProxyInvCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ProtocolizeCommand(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TrafficCommand());
    }

    public NettyPipelineInjector getNettyPipelineInjector() {
        return nettyPipelineInjector;
    }
}
