package de.exceptionflug.protocolize.items;

import de.exceptionflug.protocolize.api.ClickType;

import java.util.HashMap;
import java.util.Map;

public class InventoryAction {

  private final Map<Integer, ItemStack> itemStackMap = new HashMap<>();
  private ItemStack previousCursor, newCursor;
  private boolean beginDragging, endDragging, dragAction;
  private int dragSlot;
  private ClickType dragType;

  public ItemStack getPreviousCursor() {
    return previousCursor;
  }

  public void setPreviousCursor(final ItemStack previousCursor) {
    this.previousCursor = previousCursor;
  }

  public void setItem(final int slot, final ItemStack stack) {
    itemStackMap.put(slot, stack);
  }

  public void removeItem(final int slot) {
    itemStackMap.put(slot, null);
  }

  public Map<Integer, ItemStack> getChanges() {
    return itemStackMap;
  }

  public ItemStack getNewCursor() {
    return newCursor;
  }

  public void setNewCursor(final ItemStack newCursor) {
    this.newCursor = newCursor;
  }

  public void beginDragging() {
    beginDragging = true;
  }

  public boolean isBeginningDragging() {
    return beginDragging;
  }

  public void endDragging(final ClickType type) {
    endDragging = true;
    dragType = type;
  }

  public boolean isEndingDragging() {
    return endDragging;
  }

  public int getDragSlot() {
    return dragSlot;
  }

  public void setDragSlot(final int slot) {
    dragAction = true;
    dragSlot = slot;
  }

  public boolean isDragAction() {
    return dragAction;
  }

  public ClickType getDragType() {
    return dragType;
  }
}
