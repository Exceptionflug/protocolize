package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import net.querz.nbt.tag.Tag;

public interface LockComponent extends StructuredComponent {

    Tag<?> getLock();

    void setLock(Tag<?> lock);

    static LockComponent create(Tag<?> lock) {
        return Protocolize.getService(Factory.class).create(lock);
    }

    interface Factory {

        LockComponent create(Tag<?> lock);

    }

}
