package dev.simplix.protocolize.api.listener;

import dev.simplix.protocolize.api.Direction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

/**
 * Used for listening to incoming and outgoing packets.
 * <br><br>
 * Date: 20.08.2021
 *
 * @param <T> The packet type
 * @author Exceptionflug
 */
@Getter
@Setter
@Accessors(fluent = true)
public abstract class AbstractPacketListener<T> {

    private final Class<T> type;
    private final Direction direction;
    private final int priority;

    private Consumer<PacketReceiveEvent<T>> onReceive;
    private Consumer<PacketSendEvent<T>> onSend;

    private transient boolean registered;

    /**
     * Creates an instance of your listener.
     *
     * @param type      The concrete class of the packet to listen to
     * @param direction The stream direction
     * @param priority  The priority of the listener. Lower priority listeners will be executed first.
     */
    protected AbstractPacketListener(Class<T> type, Direction direction, int priority) {
        this.type = type;
        this.direction = direction;
        this.priority = priority;
    }

    /**
     * Gets called when a packet has been processed by the Protocolize decoder handler.
     *
     * @param event The event containing information about the packet
     */
    public void packetReceive(PacketReceiveEvent<T> event) {
        if (onReceive == null) {
            packetEvent(event);
        } else {
            onReceive.accept(event);
        }
    }

    /**
     * Gets called when a packet has been processed by the Protocolize encoder handler.
     *
     * @param event The event containing information about the packet
     */
    public void packetSend(PacketSendEvent<T> event) {
        if (onSend == null) {
            packetEvent(event);
        } else {
            onSend.accept(event);
        }
    }

    /**
     * Gets called when a packet has been processed by any Protocolize handler.
     *
     * @param event The event containing information about the packet
     */
    public void packetEvent(AbstractPacketEvent<T> event) {
        // empty default method
    }
}
