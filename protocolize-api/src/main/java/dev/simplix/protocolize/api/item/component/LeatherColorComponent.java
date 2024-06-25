package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface LeatherColorComponent extends StructuredComponent {

    int getColor();

    void setColor(int color);

    static LeatherColorComponent create(int color) {
        return Protocolize.getService(Factory.class).create(color);
    }

    interface Factory {

        LeatherColorComponent create(int color);

    }


}
