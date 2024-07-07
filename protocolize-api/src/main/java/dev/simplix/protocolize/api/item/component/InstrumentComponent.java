package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.SoundEvent;
import dev.simplix.protocolize.data.Instrument;

public interface InstrumentComponent extends StructuredComponent {

    Instrument getInstrument();

    void setInstrument(Instrument instrument);

    SoundEvent getSoundEvent();

    void setSoundEvent(SoundEvent sound);

    float getDuration();

    void setDuration(float duration);

    float getRange();

    void setRange(float range);

    static InstrumentComponent create(Instrument instrument) {
        return Protocolize.getService(Factory.class).create(instrument);
    }

    static InstrumentComponent create(SoundEvent soundEvent, float duration, float range) {
        return Protocolize.getService(Factory.class).create(soundEvent, duration, range);
    }

    interface Factory {

        InstrumentComponent create(Instrument instrument);
        InstrumentComponent create(SoundEvent soundEvent, float duration, float range);

    }

}
