package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.data.MobEffect;

import java.util.Map;

public interface SuspiciousStewEffectsComponent extends StructuredComponent {

    Map<MobEffect, Integer> getEffects();

    void setEffects(Map<MobEffect, Integer> effects);

    void addEffect(MobEffect effect, int duration);

    void removeEffect(MobEffect effect);

    void removeAllEffects();

    static SuspiciousStewEffectsComponent create(Map<MobEffect, Integer> effects) {
        return Protocolize.getService(SuspiciousStewEffectsComponent.Factory.class).create(effects);
    }

    interface Factory {

        SuspiciousStewEffectsComponent create(Map<MobEffect, Integer> effects);

    }

}
