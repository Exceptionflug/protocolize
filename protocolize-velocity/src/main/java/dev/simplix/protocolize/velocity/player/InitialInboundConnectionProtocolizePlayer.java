package dev.simplix.protocolize.velocity.player;

import com.velocitypowered.proxy.connection.MinecraftConnection;
import com.velocitypowered.proxy.connection.client.InitialInboundConnection;
import dev.simplix.protocolize.api.*;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.velocity.packet.VelocityProtocolizePacket;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public class InitialInboundConnectionProtocolizePlayer implements ProtocolizePlayer {

    private static final ProtocolRegistrationProvider REGISTRATION_PROVIDER = Protocolize.protocolRegistration();
    private static Field connectionField;

    static {
        try {
            connectionField = InitialInboundConnection.class.getDeclaredField("connection");
            connectionField.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            log.error("Unable to initialize InitialInboundConnectionProtocolizePlayer. Maybe your velocity version is unsupported.", e);
        }
    }

    private final InitialInboundConnection connection;

    public InitialInboundConnectionProtocolizePlayer(InitialInboundConnection connection) {
        this.connection = connection;
    }

    @Override
    public UUID uniqueId() {
        throw new IllegalStateException("Not possible for initial inbound connections");
    }

    @Override
    public PlayerInventory proxyInventory() {
        throw new IllegalStateException("Not possible for initial inbound connections");
    }

    @Override
    public void sendPacket(Object packet) {
        sendPacket(packet, Protocol.STATUS);
    }

    @Override
    public void sendPacket(Object packet, Protocol protocol) {
        if (packet instanceof AbstractPacket) {
            VelocityProtocolizePacket pack = (VelocityProtocolizePacket) REGISTRATION_PROVIDER.createPacket((Class<? extends AbstractPacket>) packet.getClass(),
                protocol, PacketDirection.CLIENTBOUND, protocolVersion());
            if (pack == null) {
                throw new IllegalStateException("Cannot send " + packet.getClass().getName() + " to players with protocol version " + protocolVersion());
            }
            pack.wrapper((AbstractPacket) packet);
            packet = pack;
        }
        Object finalPacket = packet;
        try {
            MinecraftConnection minecraftConnection = (MinecraftConnection) connectionField.get(connection);
            minecraftConnection.write(finalPacket);
        } catch (IllegalAccessException e) {
            log.error("Unable to send packet to InitialInboundConnectionProtocolizePlayer. Maybe your velocity version is unsupported.", e);
        }
    }

    @Override
    public void sendPacketToServer(Object packet, Protocol protocol) {
        throw new IllegalStateException("Not possible for initial inbound connections");
    }

    @Override
    public Map<Integer, Inventory> registeredInventories() {
        return Collections.emptyMap();
    }

    @Override
    public int generateWindowId() {
        throw new IllegalStateException("Not possible for initial inbound connections");
    }

    @Override
    public int protocolVersion() {
        return connection.getProtocolVersion().getProtocol();
    }

    @Override
    public <T> T handle() {
        return (T) connection;
    }

    @Override
    public Location location() {
        throw new IllegalStateException("Not possible for initial inbound connections");
    }

    @Override
    public void onInteract(Consumer<PlayerInteract> interactConsumer) {
        throw new IllegalStateException("Not possible for initial inbound connections");
    }

    @Override
    public void handleInteract(PlayerInteract interact) {
        throw new IllegalStateException("Not possible for initial inbound connections");
    }
}
