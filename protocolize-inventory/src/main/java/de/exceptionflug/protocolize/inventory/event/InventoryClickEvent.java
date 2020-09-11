package de.exceptionflug.protocolize.inventory.event;

import de.exceptionflug.protocolize.api.ClickType;
import de.exceptionflug.protocolize.api.util.ReflectionUtil;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.util.InventoryUtil;
import de.exceptionflug.protocolize.items.InventoryManager;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import de.exceptionflug.protocolize.items.PlayerInventory;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class InventoryClickEvent extends Event {

  private final ProxiedPlayer player;
  private final Inventory inventory;
  private ClickType clickType;
  private int slot, windowId, actionNumber;
  private boolean cancelled;

  public InventoryClickEvent(final ProxiedPlayer player, final Inventory inventory, final int slot, final int windowId, final ClickType clickType, final int actionNumber) {
    this.player = player;
    this.inventory = inventory;
    this.slot = slot;
    this.windowId = windowId;
    this.clickType = clickType;
    this.actionNumber = actionNumber;
  }

  public ProxiedPlayer getPlayer() {
    return player;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public ClickType getClickType() {
    return clickType;
  }

  public void setClickType(final ClickType clickType) {
    this.clickType = clickType;
  }

  public int getSlot() {
    return slot;
  }

  public void setSlot(final int slot) {
    this.slot = slot;
  }

  public int getWindowId() {
    return windowId;
  }

  public void setWindowId(final int windowId) {
    this.windowId = windowId;
  }

  public int getActionNumber() {
    return actionNumber;
  }

  public void setActionNumber(final int actionNumber) {
    this.actionNumber = actionNumber;
  }

  public ItemStack getClickedItem() {
    final ItemStack item = inventory.getItem(slot);
    final int protocolVersion = ReflectionUtil.getProtocolVersion(player);
    if (InventoryUtil.isLowerInventory(slot, inventory, protocolVersion) && (item == null || item.getType() == ItemType.NO_DATA)) {
      final PlayerInventory playerInventory = InventoryManager.getInventory(player.getUniqueId());
      final int pSlot = inventory.getType().getTypicalSize(protocolVersion) + 9;
      return playerInventory.getItem(pSlot);
    }
    return item;
  }

  public boolean isCancelled() {
    return cancelled;
  }

  public void setCancelled(final boolean cancelled) {
    this.cancelled = cancelled;
  }
}
