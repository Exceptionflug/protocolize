package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.DyeColor;

public interface BaseColorComponent extends StructuredComponent {

    DyeColor getColor();

    void setColor(DyeColor color);

    static BaseColorComponent create(DyeColor color) {
        return Protocolize.getService(Factory.class).create(color);
    }


    interface Factory {

        BaseColorComponent create(DyeColor color);

    }


}
