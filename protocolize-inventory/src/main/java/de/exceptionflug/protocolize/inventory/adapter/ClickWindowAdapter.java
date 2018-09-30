package de.exceptionflug.protocolize.inventory.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.inventory.event.InventoryClickEvent;
import de.exceptionflug.protocolize.inventory.packet.ClickWindow;
import de.exceptionflug.protocolize.items.InventoryManager;
import de.exceptionflug.protocolize.items.ItemStack;
import net.md_5.bungee.api.ProxyServer;

public class ClickWindowAdapter extends PacketAdapter<ClickWindow> {

    public ClickWindowAdapter() {
        super(Stream.UPSTREAM, ClickWindow.class);
    }

    @Override
    public void receive(final PacketReceiveEvent<ClickWindow> event) {
        final ClickWindow clickWindow = event.getPacket();
        final Inventory inventory = InventoryModule.getInventory(event.getPlayer().getUniqueId(), clickWindow.getWindowId());

        short slot = clickWindow.getSlot();
        if(inventory.getType() == InventoryType.BREWING_STAND && ReflectionUtil.getProtocolVersion(event.getPlayer()) == 47) {
            // Insert missing slot 4 for 1.8 clients into slot calculation
            if(slot >= 4)
                slot ++;
        }

        final InventoryClickEvent clickEvent = new InventoryClickEvent(event.getPlayer(), inventory, slot, clickWindow.getWindowId(), clickWindow.getClickType(), clickWindow.getActionNumber());
        ProxyServer.getInstance().getPluginManager().callEvent(clickEvent);

        if(clickEvent.isCancelled() || inventory.isHomebrew()) {
            event.setCancelled(true);
            if(inventory.getType() != InventoryType.PLAYER)
                InventoryModule.sendInventory(event.getPlayer(), inventory);
            else
                InventoryManager.getInventory(event.getPlayer().getUniqueId()).update();
            return;
        }
        if(clickWindow.getActionNumber() != clickEvent.getActionNumber()) {
            clickWindow.setActionNumber(clickEvent.getActionNumber());
            event.markForRewrite();
        }
        if(clickWindow.getWindowId() != clickEvent.getWindowId()) {
            clickWindow.setWindowId(clickEvent.getWindowId());
            event.markForRewrite();
        }
        if(clickWindow.getClickType() != clickEvent.getClickType()) {
            clickWindow.setClickType(clickEvent.getClickType());
            event.markForRewrite();
        }
        if(slot != clickEvent.getSlot()) {
            clickWindow.setSlot((short) clickEvent.getSlot());
            event.markForRewrite();
        }
    }
}
