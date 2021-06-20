package de.exceptionflug.protocolize.items;

import net.md_5.bungee.api.ProxyServer;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.MINECRAFT_1_13;

public enum ItemType {

  NO_DATA(new ItemIDMapping(0, 0, -1));

  private final ItemIDMapping[] mappings;
  private final int maxStackSize;

  ItemType(final ItemIDMapping... mappings) {
    this(64, mappings);
  }

  ItemType(final int maxStackSize, final ItemIDMapping... mappings) {
    this.maxStackSize = maxStackSize;
    this.mappings = mappings;
  }

  public static ItemType getType(final int id, final int protocolVersion, final ItemStack stack) {
    return getType(id, (short) 0, protocolVersion, stack);
  }

  public static ItemType getType(
          final int id,
          short durability,
          final int protocolVersion,
          final ItemStack stack) {
    if (protocolVersion >= MINECRAFT_1_13)
      durability = 0;
    for (final ItemType type : values()) {
      final ItemIDMapping mapping = type.getApplicableMapping(protocolVersion);
      if (mapping != null) {
        if (mapping instanceof AbstractCustomItemIDMapping) {
          if (((AbstractCustomItemIDMapping) mapping).isApplicable(
                  stack,
                  protocolVersion,
                  id,
                  durability)) {
            return type;
          }
        } else {
          if (mapping.getId() == id && mapping.getData() == durability) {
            return type;
          }
        }
      }
    }
    if (durability != 0) {
      return getType(id, protocolVersion, stack);
    }
    if (!ItemsModule.isDisableWarnOnUnknownItemMapping()) {
      ProxyServer
              .getInstance()
              .getLogger()
              .finest("[Protocolize] Don't know what item "
                      + id
                      + ":"
                      + durability
                      + " is at version "
                      + protocolVersion);
    }
    return null;
  }

  public int getMaxStackSize() {
    return maxStackSize;
  }

  public ItemIDMapping getApplicableMapping(final int protocolVersion) {
    for (final ItemIDMapping mapping : mappings) {
      if (mapping.getProtocolVersionRangeStart() <= protocolVersion
              && mapping.getProtocolVersionRangeEnd() >= protocolVersion)
        return mapping;
    }
    return null;
  }

}
