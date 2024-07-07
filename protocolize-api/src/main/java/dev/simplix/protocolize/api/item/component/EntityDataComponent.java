package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import net.querz.nbt.tag.CompoundTag;

public interface EntityDataComponent extends StructuredComponent {

    CompoundTag getData();

    void setData(CompoundTag data);

    static EntityDataComponent create(CompoundTag data) {
        return Protocolize.getService(Factory.class).create(data);
    }

    interface Factory {

        EntityDataComponent create(CompoundTag data);

    }

}
