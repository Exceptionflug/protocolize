package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface FireResistantComponent extends StructuredComponent {

    static FireResistantComponent create() {
        return Protocolize.getService(Factory.class).create();
    }

    interface Factory {

        FireResistantComponent create();

    }

}
