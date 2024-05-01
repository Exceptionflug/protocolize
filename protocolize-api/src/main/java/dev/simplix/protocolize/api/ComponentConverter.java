package dev.simplix.protocolize.api;

import net.querz.nbt.tag.Tag;

/**
 * Date: 24.08.2021
 *
 * @author Exceptionflug
 */
public interface ComponentConverter<T> {

    String toLegacyText(T component);

    String toJson(T component);

    Tag<?> toNbt(T component);

    T fromLegacyText(String legacyText);

    T fromJson(String json);

    T fromNbt(Tag<?> tag);

    void disableItalic(T component);

}
