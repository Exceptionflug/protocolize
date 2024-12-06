package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import net.querz.nbt.tag.CompoundTag;

public interface IntangibleProjectileComponent extends StructuredComponent {

    static IntangibleProjectileComponent create() {
        return Protocolize.getService(Factory.class).create();
    }

    interface Factory {

        IntangibleProjectileComponent create();

    }

}
