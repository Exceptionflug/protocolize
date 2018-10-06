package de.exceptionflug.protocolize.api.event;

import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class PacketSendEvent<T extends DefinedPacket> extends Event {

    private final Connection connection;
    private final AbstractPacketHandler packetHandler;
    private T packet;
    private boolean cancelled;
    private ServerInfo serverInfo;

    public PacketSendEvent(final Connection connection, final AbstractPacketHandler packetHandler, final T packet) {
        this.connection = connection;
        this.packetHandler = packetHandler;
        this.packet = packet;
        if(isSentToPlayer()) {
            if(getPlayer() == null)
                return;
            final Server server = getPlayer().getServer();
            if(server == null)
                return;
            serverInfo = server.getInfo();
        } else {
            serverInfo = ReflectionUtil.getServerInfo(packetHandler);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isSentToPlayer() {
        return connection instanceof PendingConnection;
    }

    public boolean isSentToServer() {
        return connection instanceof ProxiedPlayer;
    }

    public T getPacket() {
        return packet;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setPacket(final T packet) {
        this.packet = packet;
    }

    public ProxiedPlayer getPlayer() {
        if(isSentToPlayer())
            return ProxyServer.getInstance().getPlayer(((PendingConnection)connection).getUniqueId());
        if(isSentToServer())
            return (ProxiedPlayer) connection;
        return null;
    }

    public AbstractPacketHandler getPacketHandler() {
        return packetHandler;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
