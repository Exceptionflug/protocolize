package dev.simplix.protocolize.api.listener;

import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * This class is containing information about the received packet.
 * <br><br>
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
@ToString
public class PacketReceiveEvent<T> {

    private final ProtocolizePlayer player;
    private final Object server;
    private T packet;
    private boolean cancelled;
    private boolean dirty;

    /**
     * The platform {@link ProtocolizePlayer} instance.
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

    /**
     * This marks the packet ready for rewriting. Rewriting is necessary when manipulating packets on receive.
     * This does the same as {@code event.dirty(true)}.
     */
    public void markForRewrite() {
        dirty = true;
    }

}
