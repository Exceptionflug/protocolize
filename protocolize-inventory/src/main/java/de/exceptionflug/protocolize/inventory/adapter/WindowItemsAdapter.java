package de.exceptionflug.protocolize.inventory.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.inventory.util.InventoryUtil;
import de.exceptionflug.protocolize.items.InventoryManager;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import de.exceptionflug.protocolize.items.PlayerInventory;
import de.exceptionflug.protocolize.items.packet.WindowItems;
import net.md_5.bungee.api.ProxyServer;

public class WindowItemsAdapter extends PacketAdapter<WindowItems> {

    public WindowItemsAdapter() {
        super(Stream.DOWNSTREAM, WindowItems.class);
    }

    @Override
    public void receive(final PacketReceiveEvent<WindowItems> event) {
        final WindowItems packet = event.getPacket();
        if(packet.getWindowId() == 0)
            return;
        final Inventory inventory = InventoryModule.getInventory(event.getPlayer().getUniqueId(), packet.getWindowId());
        if(inventory == null) {
            ProxyServer.getInstance().getLogger().warning("[Protocolize] Try to set items in unknown inventory! player = "+event.getPlayer().getName()+" packet = "+packet);
            return;
        }
        final PlayerInventory playerInventory = InventoryManager.getInventory(event.getPlayer().getUniqueId());
        for (int i = 0; i < packet.getItems().size(); i++) {
            if(InventoryUtil.isLowerInventory(i, inventory)) {
                // LOWER PLAYER INVENTORY
                final int playerSlot = i - inventory.getSize() + 9;
                final ItemStack stack = packet.getItemStackAtSlot(i);
                final ItemStack item = playerInventory.getItem(playerSlot);
                if(stack == null || stack.getType() == ItemType.NO_DATA) {
                    if(item != null && !item.isHomebrew()) {
                        playerInventory.removeItem(playerSlot);
                    }
                } else {
                    playerInventory.setItem(playerSlot, stack);
                }
                if(packet.setItemStackAtSlot(i, item)) {
                    event.markForRewrite();
                }
            } else {
                // CONTAINER
                final ItemStack stack = packet.getItemStackAtSlot(i);
                if(stack == null || stack.getType() == ItemType.NO_DATA) {
                    if(inventory.getItem(i) != null && !inventory.getItem(i).isHomebrew()) {
                        inventory.removeItem(i);
                    }
                } else {
                    inventory.setItem(i, stack);
                }
                if(packet.setItemStackAtSlot(i, inventory.getItem(i))) {
                    event.markForRewrite();
                }
            }
        }
    }

}
