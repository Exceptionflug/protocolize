package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import net.querz.nbt.tag.CompoundTag;

public interface CustomDataComponent extends StructuredComponent {

    CompoundTag getCustomData();

    void setCustomData(CompoundTag customData);

    static CustomDataComponent create(CompoundTag customData) {
        return Protocolize.getService(Factory.class).create(customData);
    }

    interface Factory {

        CustomDataComponent create(CompoundTag customData);

    }

}
