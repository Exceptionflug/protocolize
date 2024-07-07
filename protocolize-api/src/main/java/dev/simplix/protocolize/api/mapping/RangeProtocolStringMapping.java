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
public class RangeProtocolStringMapping extends AbstractProtocolMapping implements ProtocolStringMapping {

    private final String id;
    private final int protocolId;

    RangeProtocolStringMapping(int protocolVersionStart, int protocolVersionEnd, String id, int protocolId) {
        super(protocolVersionStart, protocolVersionEnd);
        this.id = id;
        this.protocolId = protocolId;
    }

    @Override
    public String toString() {
        return "(" + protocolRangeStart() + "-" + protocolRangeEnd() + ": " + id + ":" + protocolId + ")";
    }
}
