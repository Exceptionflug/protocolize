package de.exceptionflug.protocolize.items;

public enum ItemFlag {

  HIDE_ENCHANTMENTS(0),
  HIDE_ATTRIBUTES(1),
  HIDE_UNBREAKABLE(2),
  HIDE_CAN_DESTROY(3),
  HIDE_CAN_PLACE_ON(4),
  OTHER(5);

  private final int bitIndex;

  ItemFlag(final int bitIndex) {
    this.bitIndex = bitIndex;
  }

  public int getBitIndex() {
    return bitIndex;
  }
}
