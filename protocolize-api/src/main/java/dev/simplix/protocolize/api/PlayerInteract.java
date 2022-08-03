package dev.simplix.protocolize.api;

import dev.simplix.protocolize.api.item.BaseItemStack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Date: 31.08.2021
 *
 * @author Exceptionflug
 */
@Data
@Accessors(fluent = true)
@AllArgsConstructor
public class PlayerInteract {

    private BaseItemStack currentItem;
    private BlockPosition clickedBlockPosition;
    private Hand hand;
    private boolean cancelled;

}
