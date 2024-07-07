package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.Firework;

public interface FireworkExplosionComponent extends StructuredComponent {

    Firework.Meta getExplosion();

    void setExplosion(Firework.Meta explosion);

    static FireworkExplosionComponent create(Firework.Meta explosion) {
        return Protocolize.getService(Factory.class).create(explosion);
    }

    interface Factory {

        FireworkExplosionComponent create(Firework.Meta explosion);

    }

}
