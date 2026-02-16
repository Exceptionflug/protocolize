package dev.simplix.protocolize.velocity.player;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.StateRegistry;
import com.velocitypowered.proxy.protocol.netty.MinecraftDecoder;
import com.velocitypowered.proxy.protocol.netty.MinecraftEncoder;
import dev.simplix.protocolize.api.*;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.velocity.packet.VelocityProtocolizePacket;
import dev.simplix.protocolize.velocity.util.ConversionUtils;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Date: 26.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class VelocityProtocolizePlayer implements ProtocolizePlayer {

    private static final ProtocolRegistrationProvider REGISTRATION_PROVIDER = Protocolize.protocolRegistration();
    protected static Field decoderStateField, encoderStateField;

    static {
        try {
            decoderStateField = MinecraftDecoder.class.getDeclaredField("state");
            decoderStateField.setAccessible(true);
            encoderStateField = MinecraftEncoder.class.getDeclaredField("state");
            encoderStateField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private final List<Consumer<PlayerInteract>> interactConsumers = new ArrayList<>();
    private final AtomicInteger windowId = new AtomicInteger(101);
    private final Map<Integer, Inventory> registeredInventories = new ConcurrentHashMap<>();
    private final PlayerInventory proxyInventory = new PlayerInventory(this);
    private final UUID uniqueId;
    private final ProxyServer proxyServer;
    private final Location location = new Location(0, 0, 0, 0, 0);

    public VelocityProtocolizePlayer(ProxyServer proxyServer, UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.proxyServer = proxyServer;
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
        proxyServer.getPlayer(uniqueId).ifPresent(player -> {
            ((ConnectedPlayer) player).getConnection().write(finalPacket);
        });
    }

    @Override
    public void sendPacketToServer(Object packet, Protocol protocol) {
        if (packet instanceof AbstractPacket) {
            VelocityProtocolizePacket pack = (VelocityProtocolizePacket) REGISTRATION_PROVIDER.createPacket((Class<? extends AbstractPacket>) packet.getClass(),
                protocol, PacketDirection.SERVERBOUND, protocolVersion());
            if (pack == null) {
                throw new IllegalStateException("Cannot send " + packet.getClass().getName() + " to players with protocol version " + protocolVersion());
            }
            pack.wrapper((AbstractPacket) packet);
            packet = pack;
        }
        Object finalPacket = packet;
        proxyServer.getPlayer(uniqueId).flatMap(Player::getCurrentServer).ifPresent(serverConnection -> {
            ((VelocityServerConnection) serverConnection).getConnection().write(finalPacket);
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
    public Protocol decodeProtocol() {
        Player player = proxyServer.getPlayer(uniqueId).orElse(null);
        if (player != null) {
            try {
                MinecraftDecoder minecraftDecoder = ((ConnectedPlayer) player).getConnection().getChannel().pipeline().get(MinecraftDecoder.class);
                if (minecraftDecoder != null) {
                    return ConversionUtils.protocolizeProtocol((StateRegistry) decoderStateField.get(minecraftDecoder));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Protocol encodeProtocol() {
        Player player = proxyServer.getPlayer(uniqueId).orElse(null);
        if (player != null) {
            try {
                MinecraftEncoder minecraftEncoder = ((ConnectedPlayer) player).getConnection().getChannel().pipeline().get(MinecraftEncoder.class);
                if (minecraftEncoder != null) {
                    return ConversionUtils.protocolizeProtocol((StateRegistry) encoderStateField.get(minecraftEncoder));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public <T> T handle() {
        return (T) proxyServer.getPlayer(uniqueId).orElse(null);
    }

    @Override
    public void onInteract(Consumer<PlayerInteract> interactConsumer) {
        interactConsumers.add(interactConsumer);
    }

    @Override
    public void handleInteract(PlayerInteract interact) {
        interactConsumers.forEach(interactConsumer -> interactConsumer.accept(interact));
    }

}
