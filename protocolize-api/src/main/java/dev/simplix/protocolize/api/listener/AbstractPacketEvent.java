package dev.simplix.protocolize.api.listener;

import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * This class is containing information about the packet event.
 * <br><br>
 * Date: 19.11.2023
 *
 * @author Rubenicos
 */
@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
@ToString
public abstract class AbstractPacketEvent<T> {

    protected final ProtocolizePlayer player;
    protected final Object server;
    protected T packet;
    protected boolean cancelled;

    /**
     * The protocolize player instance.
     *
     * @return The player instance or null if Protocolize was unable to track down the player
     * during early communication phases like HANDSHAKE or STATUS.
     */
    public ProtocolizePlayer player() {
        return player;
    }

    /**
     * The platform dependent server info instance.
     *
     * @param <S> The type of the server info
     * @return The platform dependent server info instance or null if Protocolize was unable to track down the server
     * during early communication phases like HANDSHAKE or STATUS.
     */
    public <S> S server() {
        return (S) server;
    }
}
