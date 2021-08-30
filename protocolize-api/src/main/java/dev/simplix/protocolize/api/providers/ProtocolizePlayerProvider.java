package dev.simplix.protocolize.api.providers;

import dev.simplix.protocolize.api.player.ProtocolizePlayer;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Date: 26.08.2021
 *
 * @author Exceptionflug
 */
public interface ProtocolizePlayerProvider {

    ProtocolizePlayer player(UUID uniqueId);

    void registerOnDisconnect(Consumer<ProtocolizePlayer> consumer);

}
