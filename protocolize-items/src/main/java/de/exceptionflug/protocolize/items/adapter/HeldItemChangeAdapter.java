package de.exceptionflug.protocolize.items.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.items.InventoryManager;
import de.exceptionflug.protocolize.items.PlayerInventory;
import de.exceptionflug.protocolize.items.packet.HeldItemChange;

public class HeldItemChangeAdapter extends PacketAdapter<HeldItemChange> {

  public HeldItemChangeAdapter(final Stream stream) {
    super(stream, HeldItemChange.class);
  }

  @Override
  public void receive(final PacketReceiveEvent<HeldItemChange> event) {
    if (event.getServerInfo() == null)
      return;
    final PlayerInventory inventory = InventoryManager.getInventory(event.getPlayer().getUniqueId(), event.getServerInfo().getName());
    inventory.setHeldItem(event.getPacket().getNewSlot());
  }
}
