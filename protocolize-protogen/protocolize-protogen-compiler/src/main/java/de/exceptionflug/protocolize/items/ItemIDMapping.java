package de.exceptionflug.protocolize.items;

import de.exceptionflug.protocolize.api.AbstractProtocolIDMapping;

public class ItemIDMapping extends AbstractProtocolIDMapping {

    private final int id;
    private int data = 0;

    public ItemIDMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final int id, final int data) {
        super(protocolVersionRangeStart, protocolVersionRangeEnd);
        this.id = id;
        this.data = data;
    }

    public ItemIDMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final int id) {
        super(protocolVersionRangeStart, protocolVersionRangeEnd);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getData() {
        return data;
    }
}
