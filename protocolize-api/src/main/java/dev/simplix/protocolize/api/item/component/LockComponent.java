package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import net.querz.nbt.tag.StringTag;

public interface LockComponent extends StructuredComponent {

    StringTag getKey();

    void setKey(StringTag key);

    static LockComponent create(StringTag key) {
        return Protocolize.getService(Factory.class).create(key);
    }

    interface Factory {

        LockComponent create(StringTag key);

    }

}
