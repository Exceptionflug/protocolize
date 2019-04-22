package de.exceptionflug.protocolize.api;

public abstract class AbstractProtocolIDMapping {

    private final int protocolVersionRangeStart, protocolVersionRangeEnd;

    protected AbstractProtocolIDMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd) {
        this.protocolVersionRangeStart = protocolVersionRangeStart;
        this.protocolVersionRangeEnd = protocolVersionRangeEnd;
    }

    public int getProtocolVersionRangeEnd() {
        return protocolVersionRangeEnd;
    }

    public int getProtocolVersionRangeStart() {
        return protocolVersionRangeStart;
    }
}
