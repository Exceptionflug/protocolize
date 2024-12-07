package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface ItemModelComponent extends StructuredComponent {

    String getModel();

    void setModel(String model);

    static ItemModelComponent create(String model) {
        return Protocolize.getService(ItemModelComponent.Factory.class).create(model);
    }

    interface Factory {

        ItemModelComponent create(String model);

    }

}
