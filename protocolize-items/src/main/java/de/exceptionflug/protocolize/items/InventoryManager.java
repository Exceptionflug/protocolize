package de.exceptionflug.protocolize.items;

import com.google.common.collect.Maps;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InventoryManager {

  private final static Map<UUID, Map<String, PlayerInventory>> INVENTORY_MAP = new ConcurrentHashMap<>();

  private InventoryManager() {
  }

  public static PlayerInventory getInventory(final ProxiedPlayer player) {
    return getInventory(player.getUniqueId(), "::MASTER::");
  }

  public static PlayerInventory getInventory(final UUID uuid) {
    return getInventory(uuid, "::MASTER::");
  }

  public static PlayerInventory getInventory(final UUID uuid, final String server) {
    return INVENTORY_MAP.computeIfAbsent(uuid, id -> new ConcurrentHashMap<>()).computeIfAbsent(server, mp -> new PlayerInventory(uuid));
  }

  public static PlayerInventory getCombinedSendInventory(final UUID uuid, final String server) {
    final PlayerInventory master = getInventory(uuid);
    final PlayerInventory serverInventory = getInventory(uuid, server);
    final PlayerInventory combined = new PlayerInventory(uuid);
    for (int i = 0; i < 45; i++) {
      final ItemStack masterItem = master.getItem(i);
      final ItemStack serverItem = serverInventory.getItem(i);
      if (masterItem != null && masterItem.getType() != ItemType.NO_DATA) {
        combined.setItem(i, masterItem);
      } else if (serverItem != null && serverItem.getType() != ItemType.NO_DATA) {
        combined.setItem(i, serverItem);
      }
    }
    combined.setHeldItem(serverInventory.getHeldItem());
    return combined;
  }

  public static void unmap(final UUID uniqueId) {
    INVENTORY_MAP.remove(uniqueId);
  }

  public static void unmapServer(final UUID uniqueId, final String name) {
    final PlayerInventory inv = getInventory(uniqueId, name);
    boolean dontDelete = false;
    for (int i = 0; i < 45; i++) {
      final ItemStack stack = inv.getItem(i);
      if (stack == null || !stack.isHomebrew()) {
        inv.setItem(i, null);
      }
      if (stack != null && stack.isHomebrew()) {
        dontDelete = true;
      }
    }
    if (!dontDelete) {
      INVENTORY_MAP.get(uniqueId).remove(name);
    }
  }
}
