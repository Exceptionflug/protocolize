package de.exceptionflug.protocolize.items.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.items.InventoryManager;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import de.exceptionflug.protocolize.items.PlayerInventory;
import de.exceptionflug.protocolize.items.event.PlayerInteractEvent;
import de.exceptionflug.protocolize.items.packet.BlockPlacement;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BlockPlacementAdapter extends PacketAdapter<BlockPlacement> {

    public BlockPlacementAdapter() {
        super(Stream.UPSTREAM, BlockPlacement.class);
    }

    @Override
    public void receive(final PacketReceiveEvent<BlockPlacement> event) {
        final ProxiedPlayer p = event.getPlayer();
        final PlayerInventory playerInventory = InventoryManager.getCombinedSendInventory(p.getUniqueId(), event.getServerInfo().getName());
        final ItemStack inHand = playerInventory.getItem(playerInventory.getHeldItem()+36);
        if(inHand == null || inHand.getType() == ItemType.NO_DATA)
            return;
        final PlayerInteractEvent event1 = new PlayerInteractEvent(p, inHand, event.getPacket().getPosition(), event.getPacket().getHand());
        ProxyServer.getInstance().getPluginManager().callEvent(event1);
        if(event.getPacket().getHand() != event1.getHand()) {
            event.getPacket().setHand(event1.getHand());
            event.markForRewrite();
        }
        if(!event.getPacket().getPosition().equals(event1.getClickedBlockPosition())) {
            event.getPacket().setPosition(event1.getClickedBlockPosition());
            event.markForRewrite();
        }
        if(inHand.isHomebrew() || event1.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
