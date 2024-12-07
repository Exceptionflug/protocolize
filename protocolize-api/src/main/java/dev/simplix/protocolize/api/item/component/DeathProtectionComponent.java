package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.ConsumeEffect;

import java.util.List;

public interface DeathProtectionComponent extends StructuredComponent {

    List<ConsumeEffect.ConsumeEffectInstance> getDeathEffects();

    void setDeathEffects(List<ConsumeEffect.ConsumeEffectInstance> deathEffects);

    void addDeathEffect(ConsumeEffect.ConsumeEffectInstance deathEffect);

    void removeDeathEffect(ConsumeEffect.ConsumeEffectInstance deathEffect);

    void clearDeathEffects();

    static DeathProtectionComponent create(List<ConsumeEffect.ConsumeEffectInstance> deathEffects) {
        return Protocolize.getService(DeathProtectionComponent.Factory.class).create(deathEffects);
    }

    interface Factory {

        DeathProtectionComponent create(List<ConsumeEffect.ConsumeEffectInstance> deathEffects);

    }

}
