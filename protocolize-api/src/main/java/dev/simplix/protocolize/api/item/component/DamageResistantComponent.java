package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface DamageResistantComponent extends StructuredComponent {

    String getTypes();

    void setTypes(String types);

    static DamageResistantComponent create(String types) {
        return Protocolize.getService(DamageResistantComponent.Factory.class).create(types);
    }

    interface Factory {

        DamageResistantComponent create(String types);

    }

}
