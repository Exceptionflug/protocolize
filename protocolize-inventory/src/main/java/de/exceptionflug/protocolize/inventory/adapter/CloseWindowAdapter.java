package de.exceptionflug.protocolize.inventory.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.event.InventoryCloseEvent;
import de.exceptionflug.protocolize.inventory.packet.CloseWindow;
import net.md_5.bungee.api.ProxyServer;

public class CloseWindowAdapter extends PacketAdapter<CloseWindow> {

    public CloseWindowAdapter(final Stream stream) {
        super(stream, CloseWindow.class);
    }

    @Override
    public void receive(final PacketReceiveEvent<CloseWindow> event) {
        if(event.getPlayer() == null)
            return;
        final Inventory inv = InventoryModule.getInventory(event.getPlayer().getUniqueId(), event.getPacket().getWindowId());
        if(inv == null)
            return;
        InventoryModule.registerInventory(event.getPlayer().getUniqueId(), event.getPacket().getWindowId(), null);
        ProxyServer.getInstance().getPluginManager().callEvent(new InventoryCloseEvent(event.getPlayer(), inv, null, event.getPacket().getWindowId()));
    }
}
