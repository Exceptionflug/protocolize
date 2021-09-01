package dev.simplix.protocolize.bungee.listener;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.bungee.ProtocolizePlugin;
import dev.simplix.protocolize.bungee.netty.ProtocolizeDecoderChannelHandler;
import dev.simplix.protocolize.bungee.netty.ProtocolizeEncoderChannelHandler;
import dev.simplix.protocolize.bungee.providers.BungeeCordProtocolizePlayerProvider;
import dev.simplix.protocolize.bungee.util.ReflectionUtil;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.AbstractPacketHandler;

public class PlayerListener implements Listener {

    private final BungeeCordProtocolizePlayerProvider playerProvider = (BungeeCordProtocolizePlayerProvider) Protocolize.playerProvider();
    private final ProtocolizePlugin plugin;

    public PlayerListener(final ProtocolizePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPing(ProxyPingEvent e) {
        plugin.nettyPipelineInjector().injectBefore(e.getConnection(), "inbound-boss", "protocolize2-decoder", new ProtocolizeDecoderChannelHandler((AbstractPacketHandler) e.getConnection(), Direction.UPSTREAM));
        plugin.nettyPipelineInjector().injectAfter(e.getConnection(), "protocolize2-decoder", "protocolize2-encoder", new ProtocolizeEncoderChannelHandler((AbstractPacketHandler) e.getConnection()));
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent e) {
        if (e.isCancelled()) {
            return;
        }
        plugin.nettyPipelineInjector().injectBefore(e.getConnection(), "inbound-boss", "protocolize2-decoder", new ProtocolizeDecoderChannelHandler((AbstractPacketHandler) e.getConnection(), Direction.UPSTREAM));
        plugin.nettyPipelineInjector().injectAfter(e.getConnection(), "protocolize2-decoder", "protocolize2-encoder", new ProtocolizeEncoderChannelHandler((AbstractPacketHandler) e.getConnection()));
    }

    @EventHandler
    public void onServerDisconnect(ServerDisconnectEvent e) {
    }

    @EventHandler
    public void onServerSwitch(ServerConnectedEvent e) {
        plugin.nettyPipelineInjector().injectBefore(e.getServer(), "inbound-boss", "protocolize2-decoder", new ProtocolizeDecoderChannelHandler(ReflectionUtil.getDownstreamBridge(e.getServer()), Direction.DOWNSTREAM));
        plugin.nettyPipelineInjector().injectAfter(e.getServer(), "protocolize2-decoder", "protocolize2-encoder", new ProtocolizeEncoderChannelHandler(ReflectionUtil.getDownstreamBridge(e.getServer())));
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        playerProvider.playerDisconnect(e.getPlayer().getUniqueId());
    }

}
