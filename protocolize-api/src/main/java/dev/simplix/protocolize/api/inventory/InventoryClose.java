package dev.simplix.protocolize.api.inventory;

import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Date: 28.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class InventoryClose {

    private final ProtocolizePlayer player;
    private final Inventory inventory;
    private final Inventory newInventory;
    private final int windowId;

}
