package de.exceptionflug.protocolize.listener;

import de.exceptionflug.protocolize.ProtocolizePlugin;
import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import de.exceptionflug.protocolize.items.InventoryManager;
import de.exceptionflug.protocolize.netty.ProtocolizeDecoderChannelHandler;
import de.exceptionflug.protocolize.netty.ProtocolizeEncoderChannelHandler;
import de.exceptionflug.protocolize.netty.ProtocolizeOutboundTrafficHandler;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.AbstractPacketHandler;

public class PlayerListener implements Listener {

    private final ProtocolizePlugin plugin;

    public PlayerListener(final ProtocolizePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(final PreLoginEvent e) {
        plugin.getNettyPipelineInjector().injectBefore(e.getConnection(), "inbound-boss", "protocolize-decoder", new ProtocolizeDecoderChannelHandler((AbstractPacketHandler) e.getConnection(), Stream.UPSTREAM));
        plugin.getNettyPipelineInjector().injectAfter(e.getConnection(), "protocolize-decoder", "protocolize-encoder", new ProtocolizeEncoderChannelHandler((AbstractPacketHandler) e.getConnection()));
        plugin.getNettyPipelineInjector().injectAfter(e.getConnection(), "frame-prepender", "protocolize-outbound-traffic-monitor", new ProtocolizeOutboundTrafficHandler((AbstractPacketHandler) e.getConnection(), Stream.UPSTREAM));
    }

    @EventHandler
    public void onServerDisconnect(final ServerDisconnectEvent e) {
        InventoryManager.unmapServer(e.getPlayer().getUniqueId(), e.getTarget().getName());
    }

    @EventHandler
    public void onServerSwitch(final ServerConnectedEvent e) {
        plugin.getNettyPipelineInjector().injectBefore(e.getServer(), "inbound-boss", "protocolize-decoder", new ProtocolizeDecoderChannelHandler(ReflectionUtil.getDownstreamBridge(e.getServer()), Stream.DOWNSTREAM));
        plugin.getNettyPipelineInjector().injectAfter(e.getServer(), "protocolize-decoder", "protocolize-encoder", new ProtocolizeEncoderChannelHandler(ReflectionUtil.getDownstreamBridge(e.getServer())));
        plugin.getNettyPipelineInjector().injectAfter(e.getServer(), "frame-prepender", "protocolize-outbound-traffic-monitor", new ProtocolizeOutboundTrafficHandler(ReflectionUtil.getDownstreamBridge(e.getServer()), Stream.DOWNSTREAM));
    }

    @EventHandler
    public void onQuit(final PlayerDisconnectEvent e) {
        InventoryManager.unmap(e.getPlayer().getUniqueId());
        ProtocolAPI.getTrafficManager().uncache(e.getPlayer().getName());
    }

}
