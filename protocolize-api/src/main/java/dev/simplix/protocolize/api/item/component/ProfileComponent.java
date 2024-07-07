package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.util.Property;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface ProfileComponent extends StructuredComponent {

    @Nullable
    String getName();

    void setName(@Nullable String name);

    @Nullable
    UUID getUniqueId();

    void setUniqueId(@Nullable UUID uniqueId);

    List<Property> getProperties();

    void setProperties(List<Property> properties);

    static ProfileComponent create(String name, UUID uniqueId, List<Property> properties) {
        return Protocolize.getService(Factory.class).create(name, uniqueId, properties);
    }

    interface Factory {

        ProfileComponent create(String name, UUID uniqueId, List<Property> properties);

    }

}
