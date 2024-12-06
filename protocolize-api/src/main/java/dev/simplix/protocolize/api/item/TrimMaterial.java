package dev.simplix.protocolize.api.item;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.data.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrimMaterial {

    private String assetName;
    private ItemType ingredient;
    private float itemModelIndex;
    private Map<Object, String> overrides;
    private ChatElement<?> description;

}
