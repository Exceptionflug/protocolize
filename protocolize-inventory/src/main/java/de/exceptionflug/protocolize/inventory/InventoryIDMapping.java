package de.exceptionflug.protocolize.inventory;

import de.exceptionflug.protocolize.api.AbstractProtocolIDMapping;

public class InventoryIDMapping extends AbstractProtocolIDMapping {

    private final String legacyId;
    private final int protocolId, typicalSize;

    public InventoryIDMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final String legacyId, final int typicalSize) {
        super(protocolVersionRangeStart, protocolVersionRangeEnd);
        this.legacyId = legacyId;
        this.typicalSize = typicalSize;
        protocolId = -1;
    }

    public InventoryIDMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final int protocolId, final int typicalSize) {
        super(protocolVersionRangeStart, protocolVersionRangeEnd);
        this.protocolId = protocolId;
        this.typicalSize = typicalSize;
        legacyId = null;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public int getTypicalSize() {
        return typicalSize;
    }
}
