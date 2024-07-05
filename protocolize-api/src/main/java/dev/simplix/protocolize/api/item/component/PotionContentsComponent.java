package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.MobEffectInstance;
import dev.simplix.protocolize.data.Potion;

import java.util.List;

public interface PotionContentsComponent extends StructuredComponent {

    Potion getPotion();

    void setPotion(Potion potionId);

    Integer getCustomColor();

    void setCustomColor(Integer customColor);

    List<MobEffectInstance> getCustomEffects();

    void setCustomEffects(List<MobEffectInstance> effects);

    void addCustomEffect(MobEffectInstance effect);

    void removeCustomEffect(MobEffectInstance effect);

    void removeAllCustomEffects();

    static PotionContentsComponent create(Potion potion) {
        return Protocolize.getService(PotionContentsComponent.Factory.class).create(potion);
    }

    static PotionContentsComponent create(Potion potion, int customColor) {
        return Protocolize.getService(PotionContentsComponent.Factory.class).create(potion, customColor);
    }

    static PotionContentsComponent create(Potion potion, int customColor, List<MobEffectInstance> customEffects) {
        return Protocolize.getService(PotionContentsComponent.Factory.class).create(potion, customColor, customEffects);
    }

    static PotionContentsComponent create(Potion potion, List<MobEffectInstance> customEffects) {
        return Protocolize.getService(PotionContentsComponent.Factory.class).create(potion, customEffects);
    }

    interface Factory {

        PotionContentsComponent create(Potion potion);

        PotionContentsComponent create(Potion potion, int customColor);

        PotionContentsComponent create(Potion potion, int customColor, List<MobEffectInstance> customEffects);

        PotionContentsComponent create(Potion potion, List<MobEffectInstance> customEffects);

    }

}
