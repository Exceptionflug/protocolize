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

    RangeProtocolStringMapping(int protocolVersionStart, int protocolVersionEnd, String id) {
        super(protocolVersionStart, protocolVersionEnd);
        this.id = id;
    }

    @Override
    public String toString() {
        return "(" + protocolRangeStart() + "-" + protocolRangeEnd() + ": " + id + ")";
    }
}
