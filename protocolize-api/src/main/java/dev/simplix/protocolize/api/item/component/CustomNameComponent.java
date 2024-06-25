package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;

public interface CustomNameComponent extends StructuredComponent {

    ChatElement<?> getCustomName();

    void setCustomName(ChatElement<?> name);

    static CustomNameComponent create(ChatElement<?> name) {
        return Protocolize.getService(Factory.class).create(name);
    }

    interface Factory {

        CustomNameComponent create(ChatElement<?> name);

    }

}
