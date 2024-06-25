package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface MaxStackSizeComponent extends StructuredComponent {

    int getMaxStackSize();

    void setMaxStackSize(int maxStackSize);

    static MaxStackSizeComponent create(int maxStackSize) {
        return Protocolize.getService(Factory.class).create(maxStackSize);
    }

    interface Factory {
        MaxStackSizeComponent create(int maxStackSize);
    }

}
