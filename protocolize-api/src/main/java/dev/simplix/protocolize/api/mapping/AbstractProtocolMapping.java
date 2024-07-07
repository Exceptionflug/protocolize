package dev.simplix.protocolize.api.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public abstract class AbstractProtocolMapping implements ProtocolMapping {

    private final int protocolRangeStart;
    private int protocolRangeEnd;

    @Override
    public final void protocolRangeEnd(int protocolRangeEnd) {
        this.protocolRangeEnd = protocolRangeEnd;
    }

    public static ProtocolIdMapping rangedIdMapping(int start, int end, int id) {
        return new RangeProtocolIdMapping(start, end, id);
    }

    public static ProtocolStringMapping rangedStringMapping(int start, int end, String id, int protocolId) {
        return new RangeProtocolStringMapping(start, end, id, protocolId);
    }

}
