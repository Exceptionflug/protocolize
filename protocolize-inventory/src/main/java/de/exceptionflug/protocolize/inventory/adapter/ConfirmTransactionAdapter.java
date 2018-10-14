package de.exceptionflug.protocolize.inventory.adapter;

import de.exceptionflug.protocolize.api.ClickType;
import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.packet.ConfirmTransaction;
import de.exceptionflug.protocolize.items.*;
import net.md_5.bungee.api.ProxyServer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ConfirmTransactionAdapter extends PacketAdapter<ConfirmTransaction> {

    public ConfirmTransactionAdapter() {
        super(Stream.DOWNSTREAM, ConfirmTransaction.class);
    }

    @Override
    public void receive(final PacketReceiveEvent<ConfirmTransaction> event) {
        final ConfirmTransaction packet = event.getPacket();
        final UUID uniqueId = event.getPlayer().getUniqueId();
        final InventoryAction action = InventoryModule.getInventoryAction(uniqueId, packet.getWindowId(), packet.getActionNumber());
        if(action == null) {
            return;
        }
        if(packet.isAccepted()) {
            final PlayerInventory playerInventory = InventoryManager.getInventory(uniqueId, event.getServerInfo().getName());
            final Inventory inventory = InventoryModule.getInventory(uniqueId, packet.getWindowId());
            if(action.isBeginningDragging()) {
                playerInventory.setDragging(true);
            }
            if(action.isDragAction()) {
                playerInventory.getDraggingSlots().add(action.getDragSlot());
            }
            if(action.isEndingDragging()) {
                final ClickType type = action.getDragType();
                final ItemStack cursor = playerInventory.getOnCursor().deepClone();
                int remnant = playerInventory.getOnCursor().getAmount();
                filterSlots(playerInventory.getDraggingSlots(), cursor, inventory);
                for(final int slot : playerInventory.getDraggingSlots()) {
                    final ItemStack stack = cursor.deepClone();
                    final int i = inventory.getItem(slot) == null ? 0 : inventory.getItem(slot).getAmount();
                    computeStackSize(playerInventory.getDraggingSlots(), type, stack, i);
                    if(stack.getAmount() > stack.getType().getMaxStackSize()) {
                        stack.setAmount((byte) stack.getType().getMaxStackSize());
                    }
                    action.setItem(slot, stack);
                    remnant -= stack.getAmount() - i;
                }
                cursor.setAmount((byte) remnant);
                if(cursor.getAmount() <= 0) {
                    action.setNewCursor(null);
                } else {
                    action.setNewCursor(cursor);
                }
                playerInventory.setDragging(false);
            }
            if(packet.getWindowId() == 0) {
                InventoryManager.getInventory(ProxyServer.getInstance().getPlayer("Exceptionflausch").getUniqueId(), event.getServerInfo().getName()).apply(action);
                InventoryManager.getInventory(ProxyServer.getInstance().getPlayer("Exceptionflausch").getUniqueId(), event.getServerInfo().getName()).update();
                playerInventory.apply(action);
            } else {
                InventoryModule.getInventory(uniqueId, packet.getWindowId()).apply(action);
            }
            playerInventory.setOnCursor(action.getNewCursor());
        } else {

        }
        InventoryModule.unregisterInventoryAction(uniqueId, packet.getWindowId(), packet.getActionNumber());
    }

    private void filterSlots(final Set<Integer> draggingSlots, final ItemStack cursor, final Inventory inventory) {
        final Set<Integer> toDel = new HashSet<>();
        for(final int slot : draggingSlots) {
            final ItemStack inSlot = inventory.getItem(slot);
            final int i = inSlot == null ? 0 : inSlot.getAmount();
            boolean flag = inSlot == null || inSlot.getType() == ItemType.NO_DATA;
            if(inSlot != null && inSlot.canBeStacked(cursor)) {
                flag |= inSlot.getAmount() + i <= inSlot.getType().getMaxStackSize();
            }
            if(!flag) {
                toDel.add(slot);
            }
        }
        draggingSlots.removeAll(toDel);
    }

    private void computeStackSize(final Set<Integer> draggingSlots, final ClickType type, final ItemStack cursor, final int stackSize) {
        switch (type) {

            case DRAG_STOP_LEFT:
                cursor.setAmount((byte) Math.floor((double) cursor.getAmount() / (double) draggingSlots.size()));
                break;

            case DRAG_STOP_RIGHT:
                cursor.setAmount((byte) 1);
                break;

            case DRAG_STOP_CREATIVE_MIDDLE:
                cursor.setAmount((byte) cursor.getType().getMaxStackSize());
                break;

        }
        cursor.setAmount((byte) (cursor.getAmount()+stackSize));
    }
}
