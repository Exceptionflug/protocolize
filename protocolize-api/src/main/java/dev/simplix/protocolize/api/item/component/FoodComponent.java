package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.MobEffectInstance;

import java.util.Map;

public interface FoodComponent extends StructuredComponent {

    int getNutrition();

    void setNutrition(int nutrition);

    float getSaturation();

    void setSaturation(float saturation);

    boolean isCanAlwaysEat();

    void setCanAlwaysEat(boolean canAlwaysEat);

    float getSecondsToEat();

    void setSecondsToEat(float secondsToEat);

    BaseItemStack getUsingConvertsTo();

    void setUsingConvertsTo(BaseItemStack usingConvertsTo);

    Map<MobEffectInstance, Float> getEffects();

    void setEffects(Map<MobEffectInstance, Float> effects);

    void addEffect(MobEffectInstance effect, float probability);

    void removeEffect(MobEffectInstance effect);

    void removeAllEffects();

    static FoodComponent create(int nutrition, float saturation, boolean canAlwaysEat, float secondsToEat, BaseItemStack usingConvertsTo, Map<MobEffectInstance, Float> effects) {
        return Protocolize.getService(FoodComponent.Factory.class).create(nutrition, saturation, canAlwaysEat, secondsToEat, usingConvertsTo, effects);
    }

    interface Factory {

        FoodComponent create(int nutrition, float saturation, boolean canAlwaysEat, float secondsToEat, BaseItemStack usingConvertsTo, Map<MobEffectInstance, Float> effects);

    }

}
