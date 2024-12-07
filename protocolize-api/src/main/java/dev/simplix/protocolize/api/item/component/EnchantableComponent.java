package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface EnchantableComponent extends StructuredComponent {

    int getValue();

    void setValue(int value);

    static EnchantableComponent create(int value) {
        return Protocolize.getService(Factory.class).create(value);
    }

    interface Factory {

        EnchantableComponent create(int value);

    }

}
