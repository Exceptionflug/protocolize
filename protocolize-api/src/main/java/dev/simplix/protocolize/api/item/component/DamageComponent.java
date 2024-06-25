package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface DamageComponent extends StructuredComponent {

    int getDamage();

    void setDamage(int damage);

    static DamageComponent create(int damage) {
        return Protocolize.getService(Factory.class).create(damage);
    }

    interface Factory {

        DamageComponent create(int damage);

    }

}
