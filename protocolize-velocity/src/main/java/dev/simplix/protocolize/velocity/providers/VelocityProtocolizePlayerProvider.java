package dev.simplix.protocolize.velocity.providers;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;
import dev.simplix.protocolize.velocity.player.VelocityProtocolizePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Date: 26.08.2021
 *
 * @author Exceptionflug
 */
public final class VelocityProtocolizePlayerProvider implements ProtocolizePlayerProvider {

    private final List<Consumer<ProtocolizePlayer>> disconnectConsumers = new ArrayList<>();
    private final List<Consumer<ProtocolizePlayer>> constructConsumers = new ArrayList<>();
    private final Map<UUID, ProtocolizePlayer> playerMap = new ConcurrentHashMap<>();
    private final ProxyServer proxyServer;

    public VelocityProtocolizePlayerProvider(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public ProtocolizePlayer player(UUID uniqueId) {
        if (proxyServer.getPlayer(uniqueId).isEmpty()) {
            return null;
        }
        return playerMap.computeIfAbsent(uniqueId, uuid -> {
            VelocityProtocolizePlayer protocolizePlayer = new VelocityProtocolizePlayer(proxyServer, uniqueId);
            constructConsumers.forEach(consumer -> consumer.accept(protocolizePlayer));
            return protocolizePlayer;
        });
    }

    @Override
    public void onDisconnect(Consumer<ProtocolizePlayer> consumer) {
        disconnectConsumers.add(consumer);
    }

    @Override
    public void onConstruct(Consumer<ProtocolizePlayer> consumer) {
        constructConsumers.add(consumer);
    }

    public void playerDisconnect(UUID uuid) {
        ProtocolizePlayer remove = playerMap.remove(uuid);
        if (remove != null) {
            disconnectConsumers.forEach(consumer -> consumer.accept(remove));
        }
    }

}
