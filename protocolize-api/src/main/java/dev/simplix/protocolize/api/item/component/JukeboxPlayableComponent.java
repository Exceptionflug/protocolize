package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.SoundEvent;

public interface JukeboxPlayableComponent extends StructuredComponent {

    String getIdentifier();

    void setIdentifier(String identifier);

    Integer getSong();

    void setSong(Integer song);

    SoundEvent getSoundEvent();

    void setSoundEvent(SoundEvent soundEvent);

    ChatElement<?> getDescription();

    void setDescription(ChatElement<?> description);

    float getDuration();

    void setDuration(float duration);

    int getOutputStrength();

    void setOutputStrength(int outputStrength);

    boolean isShowInTooltip();

    void setShowInTooltip(boolean showInTooltip);

    static JukeboxPlayableComponent create(String identifier, Integer song, SoundEvent soundEvent, ChatElement<?> description, float duration, int outputStrength, boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(identifier, song, soundEvent, description, duration, outputStrength, showInTooltip);
    }

    interface Factory {

        JukeboxPlayableComponent create(String identifier, Integer song, SoundEvent soundEvent, ChatElement<?> description, float duration, int outputStrength, boolean showInTooltip);

    }

}
