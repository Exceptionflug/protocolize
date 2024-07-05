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

    private Integer materialType;
    private TrimMaterial trimMaterial;
    private Integer patternType;
    private TrimPattern trimPattern;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TrimMaterial {
        private String assetName;
        private int ingredient;
        private float itemModelIndex;
        private Map<Integer, String> overrides;
        private ChatElement<?> description;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TrimPattern {
        private String assetName;
        private int templateItem;
        private ChatElement<?> description;
        private boolean decal;
    }

}
