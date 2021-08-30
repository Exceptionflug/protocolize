package dev.simplix.protocolize.api.providers;

import com.google.common.collect.Multimap;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;

import java.util.List;

/**
 * This provider keeps track of all protocol version dependent objects and values.
 * <br><br>
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
public interface MappingProvider {

    /**
     * This registers a new protocol mapping.
     * @param mappable The object that has to be mapped
     * @param mapping The mapping
     */
    void registerMapping(Object mappable, ProtocolMapping mapping);

    /**
     * Returns a {@link ProtocolMapping} for a given object and protocol version.
     * @param mappable The mapped object
     * @param protocolVersion The protocol version to get mappings for
     * @param <T> The type of mapping
     * @return An instance of T or null if there is no mapping for this protocol version or object
     */
    <T extends ProtocolMapping> T mapping(Object mappable, int protocolVersion);

    /**
     * Returns all available mappings for a given object.
     * @param mappable The mapped object
     * @param <T> The type of mapping
     * @return A list containing all available mappings for this object
     */
    <T extends ProtocolMapping> List<T> mappings(Object mappable);

    /**
     * Returns all objects of a given type and their mappings for the specified protocol version.
     * @param type The subclass of the object to search for
     * @param protocolVersion The protocol version
     * @param <O> The type of object to search for
     * @param <T> The type of mapping
     * @return A {@link Multimap} containing the objects as keys and their mappings as values
     */
    <O, T extends ProtocolMapping> Multimap<O, T> mappings(Class<O> type, int protocolVersion);

}
