package dev.simplix.protocolize.api.chat;

import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import net.querz.nbt.tag.Tag;

public interface ChatElement<T> {

    static <T> ChatElement<T> of(T component) {
        ComponentConverter<T> converter = (ComponentConverter<T>) Protocolize.getService(ComponentConverterProvider.class).platformConverter();
        return new ChatElementImpl<>(converter, component);
    }

    static <T> ChatElement<T> ofLegacyText(String legacyText) {
        return ChatElementImpl.ofLegacyText(legacyText);
    }

    static <T> ChatElement<T> ofJson(String json) {
        return ChatElementImpl.ofJson(json);
    }

    static <T> ChatElement<T> ofNbt(Tag<?> nbt) {
        return ChatElementImpl.ofNbt(nbt);
    }

    ChatElement<T> disableItalic();

    T asComponent();

    String asJson();

    String asLegacyText();

    Tag<?> asNbt();

}
