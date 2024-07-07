package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface MapColorComponent extends StructuredComponent {

    int getColor();

    void setColor(int color);

    static MapColorComponent create(int color) {
        return Protocolize.getService(Factory.class).create(color);
    }

    interface Factory {

        MapColorComponent create(int color);

    }

}
