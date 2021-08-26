package dev.simplix.protocolize.velocity.player;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.velocity.packet.VelocityProtocolizePacket;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * Date: 26.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class VelocityProtocolizePlayer implements ProtocolizePlayer {

    private static final ProtocolRegistrationProvider REGISTRATION_PROVIDER = Protocolize.protocolRegistration();
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
                    Protocol.PLAY, PacketDirection.CLIENTBOUND, ProtocolVersions.MINECRAFT_LATEST);
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
                    Protocol.PLAY, PacketDirection.SERVERBOUND, ProtocolVersions.MINECRAFT_LATEST);
            pack.wrapper((AbstractPacket) packet);
            packet = pack;
        }
        Object finalPacket = packet;
        proxyServer.getPlayer(uniqueId).flatMap(Player::getCurrentServer).ifPresent(serverConnection -> {
            ((VelocityServerConnection)serverConnection).getConnection().write(finalPacket);
        });
    }

}
