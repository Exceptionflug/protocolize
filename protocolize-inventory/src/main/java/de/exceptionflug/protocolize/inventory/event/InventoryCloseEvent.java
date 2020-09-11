package de.exceptionflug.protocolize.inventory.event;

import de.exceptionflug.protocolize.inventory.Inventory;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class InventoryCloseEvent extends Event {

  private final ProxiedPlayer player;
  private final Inventory inventory, newInventory;
  private final int windowId;

  public InventoryCloseEvent(final ProxiedPlayer player, final Inventory inventory, final Inventory newInventory, final int windowId) {
    this.player = player;
    this.inventory = inventory;
    this.newInventory = newInventory;
    this.windowId = windowId;
  }

  public ProxiedPlayer getPlayer() {
    return player;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public Inventory getNewInventory() {
    return newInventory;
  }

  public int getWindowId() {
    return windowId;
  }
}
