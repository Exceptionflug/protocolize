package dev.simplix.protocolize.api.item.component.exception;

import dev.simplix.protocolize.api.item.component.StructuredComponentType;

public class InvalidDataComponentVersionException extends IllegalStateException {
    public InvalidDataComponentVersionException(StructuredComponentType<?> componentType, int protocolVersion) {
        super("Component " + componentType.getName() + " can not be used on protocol version " + protocolVersion);
    }
}
