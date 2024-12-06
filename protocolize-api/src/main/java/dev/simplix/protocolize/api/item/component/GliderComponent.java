package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface GliderComponent extends StructuredComponent {

    static GliderComponent create() {
        return Protocolize.getService(Factory.class).create();
    }

    interface Factory {

        GliderComponent create();

    }

}
