package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface CustomModelDataComponent extends StructuredComponent {

    int getCustomModelData();

    void setCustomModelData(int customModelData);

    static CustomModelDataComponent create(int customModelData) {
        return Protocolize.getService(Factory.class).create(customModelData);
    }

    interface Factory {

        CustomModelDataComponent create(int customModelData);

    }

}
