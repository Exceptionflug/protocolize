package de.exceptionflug.protocolize.items.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.items.InventoryManager;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemsModule;
import de.exceptionflug.protocolize.items.PlayerInventory;
import de.exceptionflug.protocolize.items.packet.SetSlot;

public class SetSlotItemsAdapter extends PacketAdapter<SetSlot> {

  public SetSlotItemsAdapter() {
    super(Stream.DOWNSTREAM, SetSlot.class);
  }

  @Override
  public void receive(final PacketReceiveEvent<SetSlot> event) {
    final SetSlot packet = event.getPacket();
    if (packet.getWindowId() != 0)
      return;
    final PlayerInventory playerInventory = InventoryManager.getCombinedSendInventory(event.getPlayer().getUniqueId(), event.getServerInfo().getName());
    final ItemStack stack = packet.getItemStack();
    if (stack == null || stack.getType() == ItemType.NO_DATA) {
      if (playerInventory.getItem(packet.getSlot()) != null && !playerInventory.getItem(packet.getSlot()).isHomebrew() && ItemsModule.isSpigotInventoryTracking())
        playerInventory.removeItem(packet.getSlot());
    } else if (ItemsModule.isSpigotInventoryTracking()) {
      playerInventory.setItem(packet.getSlot(), stack);
    }
    if (ItemsModule.isSpigotInventoryTracking()) {
      packet.setItemStack(playerInventory.getItem(packet.getSlot()));
      event.markForRewrite();
    } else {
      final ItemStack toWrite = playerInventory.getItem(packet.getSlot());
      if (toWrite != null && toWrite.isHomebrew()) {
        packet.setItemStack(toWrite);
        event.markForRewrite();
      }
    }
  }

}
