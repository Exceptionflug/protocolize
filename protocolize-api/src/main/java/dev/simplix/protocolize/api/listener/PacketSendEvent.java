package dev.simplix.protocolize.api.listener;

import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * This class is containing information about the sent packet.
 * <br><br>
 * Date: 21.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class PacketSendEvent<T> extends AbstractPacketEvent<T> {

    public PacketSendEvent(ProtocolizePlayer player, Object server, T packet, boolean cancelled) {
        super(player, server, packet, cancelled);
    }

    @Override
    public ProtocolizePlayer player() {
        return super.player();
    }

    @Override
    public <S> S server() {
        return super.server();
    }

    @Override
    public T packet() {
        return super.packet();
    }

    @Override
    public AbstractPacketEvent<T> packet(T packet) {
        return super.packet(packet);
    }

    @Override
    public boolean cancelled() {
        return super.cancelled();
    }

    @Override
    public AbstractPacketEvent<T> cancelled(boolean cancelled) {
        return super.cancelled(cancelled);
    }

    /**
     * @return Returns true when Protocolize was unable to track down the server or player
     * during early communication phases like HANDSHAKE or STATUS.
     */
    public boolean anonymous() {
        return player == null && server == null;
    }

}
