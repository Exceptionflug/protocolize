package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.Firework;

public interface FireworksComponent extends StructuredComponent {

    Firework getFirework();

    void setFirework(Firework firework);

    static FireworksComponent create(Firework firework) {
        return Protocolize.getService(Factory.class).create(firework);
    }

    interface Factory {

        FireworksComponent create(Firework firework);

    }

}
