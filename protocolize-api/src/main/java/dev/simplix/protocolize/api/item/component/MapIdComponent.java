package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface MapIdComponent extends StructuredComponent {

    int getId();

    void setId(int id);

    static MapIdComponent create(int id) {
        return Protocolize.getService(Factory.class).create(id);
    }

    interface Factory {

        MapIdComponent create(int id);

    }

}
