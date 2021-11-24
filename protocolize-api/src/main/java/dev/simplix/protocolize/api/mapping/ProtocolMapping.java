package dev.simplix.protocolize.api.mapping;

/**
 * Date: 20.08.2021
 *
 * @author Exceptionflug
 */
public interface ProtocolMapping {

    int protocolRangeStart();

    int protocolRangeEnd();

    default boolean inRange(int version) {
        return version >= protocolRangeStart() && version <= protocolRangeEnd();
    }

    String toString();

}
