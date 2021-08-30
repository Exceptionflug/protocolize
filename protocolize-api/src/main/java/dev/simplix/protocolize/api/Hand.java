package dev.simplix.protocolize.api;

public enum Hand {

    MAIN_HAND(0), OFF_HAND(1);

    private final int protocolId;

    Hand(final int protocolId) {
        this.protocolId = protocolId;
    }

    public static Hand handByProtocolId(int protocolId) {
        for (Hand hand : values()) {
            if (hand.protocolId() == protocolId)
                return hand;
        }
        return null;
    }

    public int protocolId() {
        return protocolId;
    }

}
