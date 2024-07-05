package dev.simplix.protocolize.api.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerLayer {
    private boolean direct;
    private int patternType;
    private String identifier;
    private String translationKey;
    private DyeColor color;
}
