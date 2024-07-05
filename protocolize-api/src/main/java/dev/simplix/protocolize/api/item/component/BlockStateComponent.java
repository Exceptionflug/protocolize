package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

import java.util.Map;

public interface BlockStateComponent extends StructuredComponent {

    Map<String, String> getProperties();

    void setProperties(Map<String, String> properties);

    void addProperty(String key, String value);

    void removeProperty(String key);

    void removeAllProperties();

    static BlockStateComponent create(Map<String, String> properties) {
        return Protocolize.getService(Factory.class).create(properties);
    }

    interface Factory {

        BlockStateComponent create(Map<String, String> properties);

    }

}
