package dev.simplix.protocolize.bungee.providers;

import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;
import dev.simplix.protocolize.bungee.player.BungeeCordProtocolizePlayer;
import net.md_5.bungee.api.ProxyServer;

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
public final class BungeeCordProtocolizePlayerProvider implements ProtocolizePlayerProvider {

    private final List<Consumer<ProtocolizePlayer>> consumers = new ArrayList<>();
    private final Map<UUID, ProtocolizePlayer> playerMap = new ConcurrentHashMap<>();

    @Override
    public ProtocolizePlayer player(UUID uniqueId) {
        if (ProxyServer.getInstance().getPlayer(uniqueId) == null) {
            return null;
        }
        return playerMap.computeIfAbsent(uniqueId, uuid -> new BungeeCordProtocolizePlayer(uniqueId));
    }

    @Override
    public void registerOnDisconnect(Consumer<ProtocolizePlayer> consumer) {
        consumers.add(consumer);
    }

    public void playerDisconnect(UUID uuid) {
        ProtocolizePlayer remove = playerMap.remove(uuid);
        if (remove != null) {
            consumers.forEach(consumer -> consumer.accept(remove));
        }
    }

}
