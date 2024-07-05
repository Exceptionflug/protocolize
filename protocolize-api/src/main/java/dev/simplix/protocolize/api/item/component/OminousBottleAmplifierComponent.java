package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface OminousBottleAmplifierComponent extends StructuredComponent {

    int getAmplifier();

    void setAmplifier(int amplifier);

    static OminousBottleAmplifierComponent create(int amplifier) {
        return Protocolize.getService(Factory.class).create(amplifier);
    }

    interface Factory {

        OminousBottleAmplifierComponent create(int amplifier);

    }

}
