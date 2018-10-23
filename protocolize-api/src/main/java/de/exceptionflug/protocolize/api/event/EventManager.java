package de.exceptionflug.protocolize.api.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.exceptionflug.protocolize.api.handler.PacketListener;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

public class EventManager {

    private final List<PacketListener> packetListeners = Lists.newArrayList();

    public void registerListener(final PacketListener listener) {
        Preconditions.checkNotNull(listener, "The listener cannot be null!");
        if(packetListeners.contains(listener))
            throw new IllegalStateException("Listener already registered.");
        packetListeners.add(listener);
    }

    public Entry<DefinedPacket, Boolean> handleInboundPacket(final DefinedPacket packet, final AbstractPacketHandler abstractPacketHandler) {
        Preconditions.checkNotNull(packet, "The packet cannot be null!");
        Preconditions.checkNotNull(abstractPacketHandler, "The abstractPacketHandler cannot be null!");
        final List<PacketListener> listeners = getListenersForType(packet.getClass());
        final boolean sentByServer = ReflectionUtil.serverConnectorClass.isInstance(abstractPacketHandler);
        final Connection connection = ReflectionUtil.getConnection(abstractPacketHandler, sentByServer);
        if(connection == null) {
            // Channel not initialized.
//            ProxyServer.getInstance().getLogger().warning("[Protocolize] Handling inbound packet while channel not initialized.");
            return null;
        }
        final PacketReceiveEvent event = new PacketReceiveEvent<>(connection, abstractPacketHandler, packet);
        listeners.stream().sorted(Comparator.comparingInt(PacketListener::getPriority)).filter(it -> {
            final Stream stream = it.getStream();
            if(stream == Stream.DOWNSTREAM && sentByServer) {
                return true;
            } else if(stream == Stream.UPSTREAM && !sentByServer) {
                return true;
            }
            return false;
        }).forEach(it -> {
            try {
                it.receive(event);
            } catch (final Exception e) {
                ProxyServer.getInstance().getLogger().log(Level.SEVERE,"[Protocolize] Exception caught in listener while receiving packet "+packet.getClass().getName(), e);
            }
        });
//        ProxyServer.getInstance().getPluginManager().callEvent(event);
        if(event.isCancelled())
            return null;
        return new SimpleEntry<>(event.getPacket(), event.isDirty());
    }

    public DefinedPacket handleOutboundPacket(final DefinedPacket packet, final AbstractPacketHandler abstractPacketHandler) {
        Preconditions.checkNotNull(packet, "The packet cannot be null!");
        Preconditions.checkNotNull(abstractPacketHandler, "The abstractPacketHandler cannot be null!");
        final List<PacketListener> listeners = getListenersForType(packet.getClass());
        final boolean sentToServer = ReflectionUtil.serverConnectorClass.isInstance(abstractPacketHandler);
        final Connection connection = ReflectionUtil.getConnection(abstractPacketHandler, sentToServer);
        if(connection == null) {
            // Channel not initialized.
//            ProxyServer.getInstance().getLogger().warning("Handling outbound packet while channel not initialized.");
            return packet;
        }
        final PacketSendEvent event = new PacketSendEvent<>(connection, abstractPacketHandler, packet);
        listeners.stream().sorted(Comparator.comparingInt(PacketListener::getPriority)).filter(it -> {
            final Stream stream = it.getStream();
            if(stream == Stream.DOWNSTREAM && sentToServer) {
                return true;
            } else if(stream == Stream.UPSTREAM && !sentToServer) {
                return true;
            }
            return false;
        }).forEach(it -> {
            try {
                it.send(event);
            } catch (final Exception e) {
                ProxyServer.getInstance().getLogger().log(Level.SEVERE,"[Protocolize] Exception caught in listener while sending packet "+packet.getClass().getName(), e);
            }
        });
//        ProxyServer.getInstance().getPluginManager().callEvent(event);
        if(event.isCancelled())
            return null;
        return event.getPacket();
    }

    private List<PacketListener> getListenersForType(final Class<? extends DefinedPacket> clazz) {
        Preconditions.checkNotNull(clazz, "The clazz cannot be null!");
        final List<PacketListener> out = Lists.newArrayList();
        for(final PacketListener listener : packetListeners) {
            if(listener.getPacketClass().equals(clazz))
                out.add(listener);
        }
        return out;
    }

}
