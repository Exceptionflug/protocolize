package dev.simplix.protocolize.api.inventory;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Date: 28.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class InventoryClick {

    private final ProtocolizePlayer player;
    private final Inventory inventory;
    private ClickType clickType;
    private int slot;
    private int windowId;
    private int actionNumber;

    @Setter
    private boolean cancelled;

    public ItemStack clickedItem() {
        return inventory.item(slot);
    }

}
