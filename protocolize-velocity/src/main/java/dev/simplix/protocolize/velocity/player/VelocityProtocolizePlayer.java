package dev.simplix.protocolize.velocity.player;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.velocity.packet.VelocityProtocolizePacket;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Date: 26.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class VelocityProtocolizePlayer implements ProtocolizePlayer {

    private static final ProtocolRegistrationProvider REGISTRATION_PROVIDER = Protocolize.protocolRegistration();
    private final AtomicInteger windowId = new AtomicInteger(101);
    private final Map<Integer, Inventory> registeredInventories = new ConcurrentHashMap<>();
    private final PlayerInventory proxyInventory = new PlayerInventory(this);
    private final UUID uniqueId;
    private final ProxyServer proxyServer;

    public VelocityProtocolizePlayer(ProxyServer proxyServer, UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.proxyServer = proxyServer;
    }

    @Override
    public void sendPacket(Object packet) {
        if (packet instanceof AbstractPacket) {
            VelocityProtocolizePacket pack = (VelocityProtocolizePacket) REGISTRATION_PROVIDER.createPacket((Class<? extends AbstractPacket>) packet.getClass(),
                    Protocol.PLAY, PacketDirection.CLIENTBOUND, protocolVersion());
            pack.wrapper((AbstractPacket) packet);
            packet = pack;
        }
        Object finalPacket = packet;
        proxyServer.getPlayer(uniqueId).ifPresent(player -> {
            ((ConnectedPlayer)player).getConnection().write(finalPacket);
        });
    }

    @Override
    public void sendPacketToServer(Object packet) {
        if (packet instanceof AbstractPacket) {
            VelocityProtocolizePacket pack = (VelocityProtocolizePacket) REGISTRATION_PROVIDER.createPacket((Class<? extends AbstractPacket>) packet.getClass(),
                    Protocol.PLAY, PacketDirection.SERVERBOUND, protocolVersion());
            pack.wrapper((AbstractPacket) packet);
            packet = pack;
        }
        Object finalPacket = packet;
        proxyServer.getPlayer(uniqueId).flatMap(Player::getCurrentServer).ifPresent(serverConnection -> {
            ((VelocityServerConnection)serverConnection).getConnection().write(finalPacket);
        });
    }

    @Override
    public int generateWindowId() {
        int out = windowId.incrementAndGet();
        if (out >= 200) {
            out = 101;
            windowId.set(101);
        }
        return out;
    }

    @Override
    public int protocolVersion() {
        Player player = proxyServer.getPlayer(uniqueId).orElse(null);
        if (player != null) {
            return player.getProtocolVersion().getProtocol();
        }
        return 0;
    }

    @Override
    public <T> T handle() {
        return (T) proxyServer.getPlayer(uniqueId).orElse(null);
    }

}
