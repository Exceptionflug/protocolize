package de.exceptionflug.protocolize.inventory.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
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
            if(InventoryModule.isSpigotInventoryTracking())
                ProxyServer.getInstance().getLogger().warning("[Protocolize] Try to set items in unknown inventory! player = "+event.getPlayer().getName()+" packet = "+packet);
            return;
        }
        final PlayerInventory playerInventory = InventoryManager.getCombinedSendInventory(event.getPlayer().getUniqueId(), event.getServerInfo().getName());
        int protocolVersion;
        try {
            protocolVersion = ReflectionUtil.getProtocolVersion(event.getPlayer());
        } catch (final Exception e) {
            protocolVersion = 47;
        }
        for (int i = 0; i < packet.getItems().size(); i++) {
            if(InventoryUtil.isLowerInventory(i, inventory, protocolVersion)) {
                // LOWER PLAYER INVENTORY
                final int playerSlot = i - inventory.getType().getTypicalSize(protocolVersion) + 9;
                final ItemStack stack = packet.getItemStackAtSlot(i);
                final ItemStack item = playerInventory.getItem(playerSlot);
                if(stack == null || stack.getType() == ItemType.NO_DATA) {
                    if(item != null && !item.isHomebrew() && InventoryModule.isSpigotInventoryTracking()) {
                        playerInventory.removeItem(playerSlot);
                    }
                } else if(InventoryModule.isSpigotInventoryTracking()) {
                    playerInventory.setItem(playerSlot, stack);
                }
                if(InventoryModule.isSpigotInventoryTracking()) {
                    if(packet.setItemStackAtSlot(i, item)) {
                        event.markForRewrite();
                    }
                } else {
                    if(item != null && item.isHomebrew()) {
                        if(packet.setItemStackAtSlot(i, item)) {
                            event.markForRewrite();
                        }
                    }
                }
            } else {
                // CONTAINER
                final ItemStack stack = packet.getItemStackAtSlot(i);
                if(stack == null || stack.getType() == ItemType.NO_DATA) {
                    if(inventory.getItem(i) != null && !inventory.getItem(i).isHomebrew() && InventoryModule.isSpigotInventoryTracking()) {
                        inventory.removeItem(i);
                    }
                } else if(InventoryModule.isSpigotInventoryTracking()) {
                    inventory.setItem(i, stack);
                }
                if(InventoryModule.isSpigotInventoryTracking()) {
                    if(packet.setItemStackAtSlot(i, inventory.getItem(i))) {
                        event.markForRewrite();
                    }
                } else {
                    final ItemStack toWrite = inventory.getItem(i);
                    if(toWrite != null && toWrite.isHomebrew()) {
                        if(packet.setItemStackAtSlot(i, toWrite)) {
                            event.markForRewrite();
                        }
                    }
                }
            }
        }
    }

}
