package dev.simplix.protocolize.api.item;

import dev.simplix.protocolize.api.chat.ChatElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trim {

    Integer materialType;
    TrimMaterial trimMaterial;
    Integer patternType;
    TrimPattern trimPattern;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TrimMaterial {
        String assetName;
        int ingredient;
        float itemModelIndex;
        Map<Integer, String> overrides;
        ChatElement<?> description;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TrimPattern {
        String assetName;
        int templateItem;
        ChatElement<?> description;
        boolean decal;
    }

}
