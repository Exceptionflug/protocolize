package dev.simplix.protocolize.api.listener;

import dev.simplix.protocolize.api.player.ProtocolizePlayer;
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
@ToString
public class PacketReceiveEvent<T> extends AbstractPacketEvent<T> {

    private boolean dirty;

    public PacketReceiveEvent(ProtocolizePlayer player, Object server, T packet, boolean cancelled, boolean dirty) {
        super(player, server, packet, cancelled);
        this.dirty = dirty;
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
     * This marks the packet ready for rewriting. Rewriting is necessary when manipulating packets on receive.
     * This does the same as {@code event.dirty(true)}.
     */
    public void markForRewrite() {
        dirty = true;
    }

}
