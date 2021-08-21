package dev.simplix.protocolize.api.listener;

import dev.simplix.protocolize.api.Direction;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractPacketListener<T> {

    private final Class<T> type;
    private final Direction direction;
    private final int priority;

    protected AbstractPacketListener(Class<T> type, Direction direction, int priority) {
        this.type = type;
        this.direction = direction;
        this.priority = priority;
    }

    public abstract void packetReceive(PacketReceiveEvent<T> packet);
    public abstract void packetSend(PacketSendEvent<T> event);

}
