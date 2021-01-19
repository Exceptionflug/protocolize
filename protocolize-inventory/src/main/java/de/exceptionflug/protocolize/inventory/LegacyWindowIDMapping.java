package de.exceptionflug.protocolize.inventory;

import net.md_5.bungee.api.ProxyServer;

public enum LegacyWindowIDMapping {

  CHEST(0, "minecraft:chest"),
  CRAFTING(1, "minecraft:crafting_table"),
  FURNACE(2, "minecraft:furnace"),
  DISPENSER(3, "minecraft:dispenser"),
  ENCHANTMENT(4, "minecraft:enchanting_table"),
  BREWING_STAND(5, "minecraft:brewing_stand"),
  MERCHANT(6, "minecraft:villager"),
  BEACON(7, "minecraft:beacon"),
  ANVIL(8, "minecraft:anvil"),
  HOPPER(9, "minecraft:hopper"),
  DROPPER(10, "minecraft:dropper"),
  HORSE(11, "EntityHorse");

  private final int inventoryId;
  private final String legacyId;

  LegacyWindowIDMapping(final int inventoryId, final String legacyId) {
    this.inventoryId = inventoryId;
    this.legacyId = legacyId;
  }

  public static String getLegacyId(final int inventoryId) {
    for (final LegacyWindowIDMapping window : values()) {
      if (window.getInventoryId() == inventoryId)
        return window.getLegacyId();
    }
    ProxyServer.getInstance().getLogger().warning("[Protocolize] Unknown legacy window with inventory id " + inventoryId);
    return null;
  }

  public int getInventoryId() {
    return inventoryId;
  }

  public String getLegacyId() {
    return legacyId;
  }

}
