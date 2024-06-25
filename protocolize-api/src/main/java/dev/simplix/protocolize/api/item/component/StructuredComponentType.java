package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;

import java.util.List;

public interface StructuredComponentType<T extends StructuredComponent> {

    String getName();

    List<ProtocolIdMapping> getMappings();

    T createEmpty();

}
