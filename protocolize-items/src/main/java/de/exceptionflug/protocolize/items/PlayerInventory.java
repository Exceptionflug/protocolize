package de.exceptionflug.protocolize.items;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.items.packet.HeldItemChange;
import de.exceptionflug.protocolize.items.packet.SetSlot;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public final class PlayerInventory {

    private final Map<Integer, ItemStack> map = Maps.newHashMap();
    private final Set<Integer> draggingSlots = new HashSet<>();
    private ItemStack onCursor;
    private short heldItem;
    private final UUID uuid;
    private boolean dragging;

    public PlayerInventory(final UUID uuid) {
        this.uuid = uuid;
    }

    public boolean setItem(final int slot, final ItemStack stack) {
        if(getItem(slot) != null) {
            if(getItem(slot).isHomebrew())
                return false;
        }
        map.put(slot, stack);
        return true;
    }

    public boolean removeItem(final int slot) {
        map.remove(slot);
        return true;
    }

    public ItemStack getItem(final int slot) {
        return map.get(slot);
    }

    public void update() {
//        final WindowItems items = new WindowItems((short) 0, Lists.newArrayList());
//        for (int i = 0; i <= 44; i++) {
//            ItemStack stack = getItem(i);
//            if(stack == null) {
//                stack = ItemStack.NO_DATA;
//            }
//            items.getItems().add(stack);
//        }
//        if(getPlayer() == null) return;
//        getPlayer().unsafe().sendPacket(items);
        for (int i = 0; i <= 44; i++) {
            final ItemStack stack = getItem(i);
//            if(stack == null || stack.getType() == ItemType.NO_DATA) {
//                getPlayer().unsafe().sendPacket(new SetSlot((byte) 0, (short) i, stack));
//                continue;
//            }
            getPlayer().unsafe().sendPacket(new SetSlot((byte) 0, (short) i, stack == null ? ItemStack.NO_DATA : stack));
        }

    }

    public List<ItemStack> getItemsIndexed() {
        final ItemStack[] outArray = new ItemStack[46];
        for(final Integer id : map.keySet()) {
            outArray[id] = map.get(id);
        }
        return Arrays.asList(outArray);
    }

    public List<ItemStack> getItemsIndexedContainer() {
        final ItemStack[] outArray = new ItemStack[45-9];
        for(final Integer id : map.keySet()) {
            if(id < 9 || id == 45)
                continue;
            outArray[id-9] = map.get(id);
        }
        return Arrays.asList(outArray);
    }

    public void apply(final InventoryAction action) {
        Preconditions.checkNotNull(action, "The action cannot be null!");
        for(final int slot : action.getChanges().keySet()) {
            final ItemStack stack = action.getChanges().get(slot);
            if(stack == null) {
                removeItem(slot);
            } else {
                setItem(slot, stack);
            }
        }
    }

    public ProxiedPlayer getPlayer() {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    public short getHeldItem() {
        return heldItem;
    }

    public void setHeldItem(final short heldItem) {
        this.heldItem = heldItem;
    }

    public void changeHeldItem(final short rawSlot) {
        setHeldItem((short) (rawSlot+36));
        if(getPlayer() == null) return;
        getPlayer().unsafe().sendPacket(new HeldItemChange(rawSlot));
        getPlayer().getServer().unsafe().sendPacket(new HeldItemChange(rawSlot));
    }

    public void setOnCursor(final ItemStack onCursor) {
        this.onCursor = onCursor;
    }

    public Set<Integer> getDraggingSlots() {
        return draggingSlots;
    }

    public ItemStack getOnCursor() {
        return onCursor;
    }

    public void clear() {
        map.clear();
    }

    public void setDragging(final boolean dragging) {
        if(!dragging)
            draggingSlots.clear();
        this.dragging = dragging;
    }

    public boolean isDragging() {
        return dragging;
    }

}
