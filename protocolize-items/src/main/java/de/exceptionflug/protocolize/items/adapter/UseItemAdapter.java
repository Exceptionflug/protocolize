package de.exceptionflug.protocolize.items.adapter;

import de.exceptionflug.protocolize.api.CancelSendSignal;
import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.items.InventoryManager;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import de.exceptionflug.protocolize.items.PlayerInventory;
import de.exceptionflug.protocolize.items.event.PlayerInteractEvent;
import de.exceptionflug.protocolize.items.packet.UseItem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UseItemAdapter extends PacketAdapter<UseItem> {

    public UseItemAdapter() {
        super(Stream.UPSTREAM, UseItem.class);
    }

    @Override
    public void receive(final PacketReceiveEvent<UseItem> event) {
        final ProxiedPlayer p = event.getPlayer();
        final PlayerInventory playerInventory = InventoryManager.getInventory(p.getUniqueId());
        final ItemStack inHand = playerInventory.getItem(playerInventory.getHeldItem()+36);
        if(inHand == null || inHand.getType() == ItemType.NO_DATA)
            return;
        final PlayerInteractEvent event1 = new PlayerInteractEvent(p, inHand, null, event.getPacket().getHand());
        ProxyServer.getInstance().getPluginManager().callEvent(event1);
        event.getPacket().setHand(event1.getHand());
        if(inHand.isHomebrew() || event1.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
