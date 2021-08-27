package dev.simplix.protocolize.api.inventory;

import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.packets.HeldItemChange;
import dev.simplix.protocolize.data.packets.SetSlot;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * Date: 26.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Setter
@Accessors(fluent = true)
public class PlayerInventory {

    private final ProtocolizePlayer player;
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final Set<Integer> draggingSlots = new HashSet<>();
    private ItemStack cursorItem;
    private short heldItem;
    private boolean dragging;

    public PlayerInventory(ProtocolizePlayer player) {
        this.player = player;
    }

    public void item(int slot, ItemStack stack) {
        items.put(slot, stack);
    }

    public ItemStack item(int slot) {
        return items.get(slot);
    }

    public boolean removeItem(int slot) {
        return items.remove(slot) != null;
    }

    public List<ItemStack> itemsIndexed() {
        ItemStack[] outArray = new ItemStack[46];
        for (Integer id : items.keySet()) {
            outArray[id] = items.get(id);
        }
        return Arrays.asList(outArray);
    }

    public List<ItemStack> itemsIndexedContainer() {
        ItemStack[] outArray = new ItemStack[45 - 9];
        for (Integer id : items.keySet()) {
            if (id < 9 || id == 45)
                continue;
            outArray[id - 9] = items.get(id);
        }
        return Arrays.asList(outArray);
    }

    public void heldItem(short slot) {
        heldItem = (short) (slot + 36);
        player.sendPacket(new HeldItemChange(slot));
        player.sendPacketToServer(new HeldItemChange(slot));
    }

    public void clear() {
        items.clear();
    }

    public void dragging(boolean dragging) {
        if (!dragging)
            draggingSlots.clear();
        this.dragging = dragging;
    }

    public void update() {
        for (int i = 0; i <= 44; i++) {
            ItemStack stack = item(i);
            if (stack != null) {
                player.sendPacket(new SetSlot((byte) 0, (short) i, stack, 0));
            }
        }
    }

}
