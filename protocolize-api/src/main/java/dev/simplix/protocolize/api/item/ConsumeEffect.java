package dev.simplix.protocolize.api.item;

import dev.simplix.protocolize.api.util.Either;
import dev.simplix.protocolize.data.ConsumeEffectType;
import dev.simplix.protocolize.data.MobEffect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ConsumeEffect {

    @Data
    @AllArgsConstructor
    public static class ApplyStatusEffects implements ConsumeEffectInstance {
        List<MobEffectInstance> effects;
        float probability;

        public ConsumeEffectType getType(){
            return ConsumeEffectType.APPLY_EFFECTS;
        }
    }

    @Data
    @AllArgsConstructor
    public static class RemoveStatusEffects implements ConsumeEffectInstance {
        Either<String, List<MobEffect>> effects;

        public ConsumeEffectType getType(){
            return ConsumeEffectType.REMOVE_EFFECTS;
        }
    }

    @Data
    @AllArgsConstructor
    public static class ClearAllStatusEffects implements ConsumeEffectInstance {

        public ConsumeEffectType getType(){
            return ConsumeEffectType.CLEAR_ALL_EFFECTS;
        }
    }

    @Data
    @AllArgsConstructor
    public static class TeleportRandomly implements ConsumeEffectInstance {
        float diameter;

        public ConsumeEffectType getType(){
            return ConsumeEffectType.TELEPORT_RANDOMLY;
        }
    }

    @Data
    @AllArgsConstructor
    public static class PlaySound implements ConsumeEffectInstance {
        SoundEvent sound;

        public ConsumeEffectType getType(){
            return ConsumeEffectType.PLAY_SOUND;
        }
    }

    public interface ConsumeEffectInstance {
        ConsumeEffectType getType();
    }

}
