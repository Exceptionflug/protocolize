package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;

import java.util.List;

public interface LoreComponent extends StructuredComponent {

    List<ChatElement<?>> getLore();

    void setLore(List<ChatElement<?>> lore);

    static LoreComponent create(List<ChatElement<?>> lore) {
        return Protocolize.getService(Factory.class).create(lore);
    }

    interface Factory {

        LoreComponent create(List<ChatElement<?>> lore);

    }

}
