package de.exceptionflug.protocolize.inventory.util;

import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryType;

public final class InventoryUtil {

    private InventoryUtil() {}

    public static boolean isLowerInventory(final int i, final Inventory inventory, final int protocolVersion) {
        return i >= inventory.getType().getTypicalSize(protocolVersion);
    }

}
