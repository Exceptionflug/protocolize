package dev.simplix.protocolize.api.mapping;


import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class RangeProtocolIdMapping extends AbstractProtocolMapping implements ProtocolIdMapping {

    private final int id;

    RangeProtocolIdMapping(int protocolVersionStart, int protocolVersionEnd, int id) {
        super(protocolVersionStart, protocolVersionEnd);
        this.id = id;
    }

    @Override
    public String toString() {
        return "(" + protocolRangeStart() + "-" + protocolRangeEnd() + ": " + id + " 0x" + Integer.toHexString(id) + ")";
    }
}
