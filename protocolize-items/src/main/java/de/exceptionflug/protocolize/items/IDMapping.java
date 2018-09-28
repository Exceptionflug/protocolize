package de.exceptionflug.protocolize.items;

public class IDMapping {

    private final int protocolVersionRangeStart, protocolVersionRangeEnd;
    private final int id;
    private int data = 0;

    public IDMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final int id, final int data) {
        this.protocolVersionRangeStart = protocolVersionRangeStart;
        this.protocolVersionRangeEnd = protocolVersionRangeEnd;
        this.id = id;
        this.data = data;
    }

    public IDMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final int id) {
        this.protocolVersionRangeStart = protocolVersionRangeStart;
        this.protocolVersionRangeEnd = protocolVersionRangeEnd;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getProtocolVersionRangeEnd() {
        return protocolVersionRangeEnd;
    }

    public int getProtocolVersionRangeStart() {
        return protocolVersionRangeStart;
    }

    public int getData() {
        return data;
    }
}
