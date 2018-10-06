package de.exceptionflug.protocolize.items;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public final class InventoryManager {

    private InventoryManager() {}

    private final static Map<UUID, Map<String, PlayerInventory>> INVENTORY_MAP = Maps.newHashMap();

    public static PlayerInventory getInventory(final UUID uuid) {
        return getInventory(uuid, "::MASTER::");
    }

    public static PlayerInventory getInventory(final UUID uuid, final String server) {
        return INVENTORY_MAP.computeIfAbsent(uuid, id -> Maps.newHashMap()).computeIfAbsent(server, mp -> new PlayerInventory(uuid));
    }

    public static PlayerInventory getCombinedSendInventory(final UUID uuid, final String server) {
        final PlayerInventory master = getInventory(uuid);
        final PlayerInventory serverInventory = getInventory(uuid, server);
        final PlayerInventory combined = new PlayerInventory(uuid);
        for (int i = 0; i < 45; i++) {
            final ItemStack masterItem = master.getItem(i);
            final ItemStack serverItem = serverInventory.getItem(i);
            if(masterItem != null && masterItem.getType() != ItemType.NO_DATA) {
                combined.setItem(i, masterItem);
            } else if(serverItem != null && serverItem.getType() != ItemType.NO_DATA) {
                combined.setItem(i, serverItem);
            }
        }
        combined.setHeldItem(serverInventory.getHeldItem());
        return combined;
    }

    public static void unmap(final UUID uniqueId) {
        INVENTORY_MAP.remove(uniqueId);
    }
}
