package de.exceptionflug.protocolize.items;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public final class InventoryManager {

    private InventoryManager() {}

    private final static Map<UUID, PlayerInventory> INVENTORY_MAP = Maps.newHashMap();

    public static PlayerInventory getInventory(final UUID uuid) {
        return INVENTORY_MAP.get(uuid);
    }

    public static void map(final UUID uuid) {
        INVENTORY_MAP.put(uuid, new PlayerInventory(uuid));
    }

    public static void unmap(final UUID uniqueId) {
        INVENTORY_MAP.remove(uniqueId);
    }
}
