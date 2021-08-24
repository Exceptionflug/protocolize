package dev.simplix.protocolize.api.providers;

import dev.simplix.protocolize.api.ComponentConverter;

/**
 * Date: 24.08.2021
 *
 * @author Exceptionflug
 */
public interface ComponentConverterProvider {

    ComponentConverter<?> platformConverter();

}
