package de.exceptionflug.protocolize.items.event;

import de.exceptionflug.protocolize.api.BlockPosition;
import de.exceptionflug.protocolize.api.Hand;
import de.exceptionflug.protocolize.items.ItemStack;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public final class PlayerInteractEvent extends Event {

    private ProxiedPlayer player;
    private ItemStack currentItem;
    private BlockPosition clickedBlockPosition;
    private Hand hand;
    private boolean cancelled;

    public PlayerInteractEvent(final ProxiedPlayer player, final ItemStack currentItem) {
        this.player = player;
        this.currentItem = currentItem;
    }

    public PlayerInteractEvent(final ProxiedPlayer player, final ItemStack currentItem, final BlockPosition clickedBlockPosition) {
        this.player = player;
        this.currentItem = currentItem;
        this.clickedBlockPosition = clickedBlockPosition;
    }

    public PlayerInteractEvent(final ProxiedPlayer player, final ItemStack currentItem, final BlockPosition clickedBlockPosition, final Hand hand) {
        this.player = player;
        this.currentItem = currentItem;
        this.clickedBlockPosition = clickedBlockPosition;
        this.hand = hand;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public void setPlayer(final ProxiedPlayer player) {
        this.player = player;
    }

    public ItemStack getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(final ItemStack currentItem) {
        this.currentItem = currentItem;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public BlockPosition getClickedBlockPosition() {
        return clickedBlockPosition;
    }

    public void setClickedBlockPosition(final BlockPosition clickedBlockPosition) {
        this.clickedBlockPosition = clickedBlockPosition;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(final Hand hand) {
        this.hand = hand;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public String toString() {
        return "PlayerInteractEvent{" +
                "player=" + player +
                ", currentItem=" + currentItem +
                ", clickedBlockPosition=" + clickedBlockPosition +
                ", hand=" + hand +
                ", cancelled=" + cancelled +
                '}';
    }
}
