package de.exceptionflug.protocolize.inventory;

import net.md_5.bungee.api.ProxyServer;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public enum InventoryType {

  GENERIC_9X1(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.CHEST, 9), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 9), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 0, 9)),
  GENERIC_9X2(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.CHEST, 18), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 18), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 1, 18)),
  GENERIC_9X3(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.CHEST, 27), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 27), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 2, 27)),
  GENERIC_9X4(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.CHEST, 36), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 36), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 3, 36)),
  GENERIC_9X5(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.CHEST, 45), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 45), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 4, 45)),
  GENERIC_9X6(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.CHEST, 54), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 54), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 5, 54)),
  GENERIC_3X3(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.CHEST, 3), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:dropper", 9), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 6, 9)),
  ANVIL(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.ANVIL, 3), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:anvil", 3), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 7, 3)),
  BEACON(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.BEACON, 1), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:beacon", 1), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 8, 1)),
  BLAST_FURNACE(new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 9, 2)),
  BREWING_STAND(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.BREWING_STAND, 5), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_8, "minecraft:brewing_stand", 4), new InventoryIDMapping(MINECRAFT_1_9, MINECRAFT_1_13_2, "minecraft:brewing_stand", 5), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 10, 5)),
  CRAFTING(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.CRAFTING, 10), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:crafting_table", 10), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 11, 10)),
  ENCHANTMENT(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.ENCHANTMENT, 1), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:enchanting_table", 2), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 12, 2)),
  FURNACE(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.FURNACE, 3), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:furnace", 3), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 13, 3)),
  GRINDSTONE(new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 14, 3)),
  HOPPER(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.HOPPER, 5), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:hopper", 5), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 15, 5)),
  LECTERN(new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 16, 0)),
  LOOM(new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 17, 4)),
  MERCHANT(new InventoryIDMapping(MINECRAFT_1_7_2, MINECRAFT_1_7_6, LegacyWindow.MERCHANT, 3), new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:villager", 3), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 18, 3)),
  SHULKER_BOX(new InventoryIDMapping(MINECRAFT_1_9, MINECRAFT_1_13_2, "minecraft:shulker_box", 27), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 19, 27)),
  SMOKER(new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 20, 3)),
  CARTOGRAPHY(new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 21, 3)),
  STONECUTTER(new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 22, 2)),
  PLAYER(new InventoryIDMapping(MINECRAFT_1_8, MINECRAFT_1_8, "Player", 45), new InventoryIDMapping(MINECRAFT_1_9, MINECRAFT_1_13_2, "Player", 46), new InventoryIDMapping(MINECRAFT_1_14, MINECRAFT_LATEST, -1, 46));

  private final InventoryIDMapping[] mappings;

  InventoryType(final InventoryIDMapping... mappings) {
    this.mappings = mappings;
  }

  public static InventoryType getType(final String id, final int size, final int protocolVersion) {
    for (final InventoryType type : values()) {
      if (protocolVersion >= MINECRAFT_1_14) {
        throw new UnsupportedOperationException("Please use InventoryType#getType(id: int, protocolVersion: int): InventoryType for 1.14 protocol version.");
      }
      final String typeId = type.getLegacyTypeId(protocolVersion);
      if (typeId != null && typeId.equals(id) && type.getTypicalSize(protocolVersion) == size)
        return type;
    }
    return null;
  }

  public static InventoryType getType(final int id, final int protocolVersion) {
    for (final InventoryType type : values()) {
      if (protocolVersion < MINECRAFT_1_14) {
        throw new UnsupportedOperationException("Please use InventoryType#getType(id: String, protocolVersion: int): InventoryType for legacy protocol versions.");
      }
      final int typeId = type.getTypeId(protocolVersion);
      if (typeId != -1 && typeId == id)
        return type;
    }
    return null;
  }

  public static InventoryType getChestInventoryWithSize(final int size) {
    if (size % 9 != 0) {
      throw new IllegalArgumentException("Size must be dividable by 9");
    }
    final int rows = size / 9;
    return getChestInventoryWithRows(rows);
  }

  public static InventoryType getChestInventoryWithRows(final int rows) {
    return valueOf("GENERIC_9X" + rows);
  }

  public InventoryIDMapping[] getMappings() {
    return mappings;
  }

  public String getLegacyTypeId(final int protocolVersion) {
    for (final InventoryIDMapping mapping : mappings) {
      if (mapping.getProtocolVersionRangeStart() <= protocolVersion && mapping.getProtocolVersionRangeEnd() >= protocolVersion) {
        return mapping.getLegacyId();
      }
    }
    return null;
  }

  public int getTypeId(final int protocolVersion) {
    for (final InventoryIDMapping mapping : mappings) {
      if (mapping.getProtocolVersionRangeStart() <= protocolVersion && mapping.getProtocolVersionRangeEnd() >= protocolVersion) {
        return mapping.getProtocolId();
      }
    }
    return -1;
  }

  public int getTypicalSize(final int protocolVersion) {
    for (final InventoryIDMapping mapping : mappings) {
      if (mapping.getProtocolVersionRangeStart() <= protocolVersion && mapping.getProtocolVersionRangeEnd() >= protocolVersion) {
        return mapping.getTypicalSize();
      }
    }
    ProxyServer.getInstance().getLogger().warning("[Protocolize] Unable to find typical inventory size of " + name() + " in version " + protocolVersion);
    return -1;
  }

  public boolean isChest() {
    if (this == GENERIC_3X3)
      return false;
    return this.name().startsWith("GENERIC");
  }

}
