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

    String getCustomName();

    void setCustomName(String customName);

    static PotionContentsComponent create(Potion potion) {
        return Protocolize.getService(PotionContentsComponent.Factory.class).create(potion);
    }

    static PotionContentsComponent create(Potion potion, int customColor) {
        return Protocolize.getService(PotionContentsComponent.Factory.class).create(potion, customColor);
    }

    static PotionContentsComponent create(Potion potion, int customColor, String customName) {
        return Protocolize.getService(PotionContentsComponent.Factory.class).create(potion, customColor, customName);
    }

    static PotionContentsComponent create(Potion potion, int customColor, List<MobEffectInstance> customEffects) {
        return Protocolize.getService(PotionContentsComponent.Factory.class).create(potion, customColor, customEffects);
    }

    static PotionContentsComponent create(Potion potion, int customColor, List<MobEffectInstance> customEffects, String customName) {
        return Protocolize.getService(PotionContentsComponent.Factory.class).create(potion, customColor, customEffects, customName);
    }

    static PotionContentsComponent create(Potion potion, List<MobEffectInstance> customEffects) {
        return Protocolize.getService(PotionContentsComponent.Factory.class).create(potion, customEffects);
    }

    static PotionContentsComponent create(Potion potion, List<MobEffectInstance> customEffects, String customName) {
        return Protocolize.getService(PotionContentsComponent.Factory.class).create(potion, customEffects, customName);
    }

    interface Factory {

        PotionContentsComponent create(Potion potion);

        PotionContentsComponent create(Potion potion, int customColor);

        PotionContentsComponent create(Potion potion, int customColor, String customName);

        PotionContentsComponent create(Potion potion, int customColor, List<MobEffectInstance> customEffects);

        PotionContentsComponent create(Potion potion, int customColor, List<MobEffectInstance> customEffects, String customName);

        PotionContentsComponent create(Potion potion, List<MobEffectInstance> customEffects);

        PotionContentsComponent create(Potion potion, List<MobEffectInstance> customEffects, String customName);

    }

}
