package dev.simplix.protocolize.api.item.component.exception;

public class InvalidDataComponentTypeException extends IllegalStateException {
    public InvalidDataComponentTypeException(int componentId, int protocolVersion) {
        super("Could not find component type " + componentId + " for protocol version " +
            protocolVersion + ". This item is not compatible with Protocolize.");
    }
}
