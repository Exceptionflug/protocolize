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
    private final int protocolRangeEnd;

    public static ProtocolIdMapping createRangedIdMapping(int start, int end, int id) {
        return new RangeProtocolIdMapping(start, end, id);
    }

}
