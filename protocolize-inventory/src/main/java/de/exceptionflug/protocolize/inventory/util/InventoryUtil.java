package de.exceptionflug.protocolize.inventory.util;

import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryType;

public final class InventoryUtil {

    private InventoryUtil() {}

    public static boolean isLowerInventory(final int i, final Inventory inventory) {
        if(inventory.getType() == InventoryType.CHEST || inventory.getType() == InventoryType.CONTAINER) {
            return i >= inventory.getSize();
        } else if(inventory.getType() == InventoryType.HORSE) {
            return i >= inventory.getSize();
        } else {
            return i >= inventory.getType().getTypicalSize();
        }
    }

}
