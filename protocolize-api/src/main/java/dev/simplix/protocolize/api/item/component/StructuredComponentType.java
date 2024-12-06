package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.data.DataComponentType;

import java.util.List;

public interface StructuredComponentType<T extends StructuredComponent> {

    String getName();

    default DataComponentType getDataComponentType() {
        String identifier = getName().toUpperCase();
        if(identifier.contains(":"))
            identifier = identifier.split(":")[1];
        return DataComponentType.valueOf(identifier);
    }

    default List<ProtocolIdMapping> getMappings() {
        return Protocolize.mappingProvider().mappings(getDataComponentType());
    }

    T createEmpty();

}
