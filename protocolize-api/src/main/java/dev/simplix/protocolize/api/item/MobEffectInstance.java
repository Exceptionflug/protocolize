package dev.simplix.protocolize.api.item;

import dev.simplix.protocolize.data.MobEffect;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MobEffectInstance {
    private MobEffect mobEffect;
    private Details details;

    @Data
    @AllArgsConstructor
    public static class Details {
        private int amplifier;
        private int duration;
        private boolean ambient, showParticles, showIcon;
        private Details hiddenEffect;
    }
}
