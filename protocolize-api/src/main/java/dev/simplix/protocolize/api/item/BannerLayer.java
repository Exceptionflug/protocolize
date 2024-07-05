package dev.simplix.protocolize.api.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerLayer {
    boolean direct;
    int patternType;
    String identifier;
    String translationKey;
    DyeColor color;
}
