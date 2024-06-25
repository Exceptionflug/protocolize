package dev.simplix.protocolize.api.chat;

import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import net.querz.nbt.tag.Tag;

class ChatElementImpl<T> implements ChatElement<T> {

    private final ComponentConverter<T> componentConverter;
    private T component;

    ChatElementImpl(ComponentConverter<T> componentConverter, T component) {
        this.componentConverter = componentConverter;
        this.component = component;
    }

    static <T> ChatElement<T> ofLegacyText(String text) {
        ComponentConverter<T> converter = (ComponentConverter<T>) Protocolize.getService(ComponentConverterProvider.class).platformConverter();
        return new ChatElementImpl<>(converter, converter.fromLegacyText(text));
    }

    static <T> ChatElement<T> ofJson(String text) {
        ComponentConverter<T> converter = (ComponentConverter<T>) Protocolize.getService(ComponentConverterProvider.class).platformConverter();
        return new ChatElementImpl<>(converter, converter.fromJson(text));
    }

    static <T> ChatElement<T> ofNbt(Tag<?> tag) {
        ComponentConverter<T> converter = (ComponentConverter<T>) Protocolize.getService(ComponentConverterProvider.class).platformConverter();
        return new ChatElementImpl<>(converter, converter.fromNbt(tag));
    }

    @Override
    public ChatElement<T> disableItalic() {
        componentConverter.disableItalic(component);
        return this;
    }

    @Override
    public T asComponent() {
        return component;
    }

    @Override
    public String asJson() {
        return componentConverter.toJson(component);
    }

    @Override
    public String asLegacyText() {
        return componentConverter.toLegacyText(component);
    }

    @Override
    public Tag<?> asNbt() {
        return componentConverter.toNbt(component);
    }

    @Override
    public String toString() {
        return asLegacyText();
    }
}
