package dev.simplix.protocolize.api;

/**
 * Date: 24.08.2021
 *
 * @author Exceptionflug
 */
public interface ComponentConverter<T> {

    String toLegacyText(T component);

    String toJson(T component);

    T fromLegacyText(String legacyText);

    T fromJson(String json);

}
