package de.exceptionflug.protocolize.inventory.event;

import de.exceptionflug.protocolize.inventory.Inventory;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class InventoryOpenEvent extends Event {

    private final ProxiedPlayer player;
    private Inventory inventory;
    private int windowId;
    private boolean cancelled;

    public InventoryOpenEvent(final ProxiedPlayer player, final Inventory inventory, final int windowId) {
        this.player = player;
        this.inventory = inventory;
        this.windowId = windowId;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(final Inventory inventory) {
        this.inventory = inventory;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(final int windowId) {
        this.windowId = windowId;
    }
}
