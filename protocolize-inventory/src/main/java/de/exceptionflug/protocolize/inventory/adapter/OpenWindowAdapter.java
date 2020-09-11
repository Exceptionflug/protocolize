package de.exceptionflug.protocolize.inventory.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.event.InventoryOpenEvent;
import de.exceptionflug.protocolize.inventory.packet.OpenWindow;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class OpenWindowAdapter extends PacketAdapter<OpenWindow> {

  public OpenWindowAdapter() {
    super(Stream.DOWNSTREAM, OpenWindow.class);
  }

  @Override
  public void receive(final PacketReceiveEvent<OpenWindow> event) {
    final ProxiedPlayer p = event.getPlayer();
    final OpenWindow packet = event.getPacket();

    Inventory inventory = new Inventory(packet.getInventoryType(), packet.getTitle());
    final Inventory original = inventory;
    inventory.setHomebrew(false);
    int windowId = packet.getWindowId();

    final InventoryOpenEvent openEvent = new InventoryOpenEvent(p, inventory, windowId);
    ProxyServer.getInstance().getPluginManager().callEvent(openEvent);
    if (openEvent.isCancelled()) {
      event.setCancelled(true);
      return;
    }
    inventory = openEvent.getInventory();
    windowId = openEvent.getWindowId();
    if (!inventory.equals(original) || packet.getWindowId() != windowId) {
      event.markForRewrite();
      packet.setWindowId(windowId);
      packet.setInventoryType(inventory.getType());
      packet.setTitle(inventory.getTitle());
    }

    InventoryModule.registerInventory(p.getUniqueId(), windowId, inventory);
  }

}
