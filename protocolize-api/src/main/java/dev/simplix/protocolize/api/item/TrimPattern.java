package dev.simplix.protocolize.api.item;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.data.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrimPattern {

    private String assetName;
    private ItemType templateItem;
    private ChatElement<?> description;
    private boolean decal;

}
