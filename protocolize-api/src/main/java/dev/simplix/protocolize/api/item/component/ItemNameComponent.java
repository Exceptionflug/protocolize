package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;

public interface ItemNameComponent extends StructuredComponent {

    ChatElement<?> getName();

    void setName(ChatElement<?> name);

    static ItemNameComponent create(ChatElement<?> chatElement) {
        return Protocolize.getService(Factory.class).create(chatElement);
    }

    interface Factory {

        ItemNameComponent create(ChatElement<?> name);

    }

}
