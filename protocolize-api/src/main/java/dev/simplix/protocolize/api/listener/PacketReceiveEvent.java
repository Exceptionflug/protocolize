package dev.simplix.protocolize.api.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
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

    private final Object player;
    private final Object server;
    private T packet;
    private boolean cancelled;
    private boolean dirty;

    public <P> P player() {
        return (P) player;
    }

    public <S> S server() {
        return (S) server;
    }

    /**
     * This marks the packet ready for rewriting.
     * This does the same like calling dirty(true).
     */
    public void markForRewrite() {
        dirty = true;
    }

}
