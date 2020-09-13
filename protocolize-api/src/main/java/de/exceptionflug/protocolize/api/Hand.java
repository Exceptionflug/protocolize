package de.exceptionflug.protocolize.api;

public enum Hand {

  MAIN_HAND(0), OFF_HAND(1);

  private final int protocolId;

  Hand(final int protocolId) {
    this.protocolId = protocolId;
  }

  public static Hand getHandByID(final int protocolId) {
    for (final Hand hand : values()) {
      if (hand.getProtocolId() == protocolId)
        return hand;
    }
    return null;
  }

  public int getProtocolId() {
    return protocolId;
  }

}
