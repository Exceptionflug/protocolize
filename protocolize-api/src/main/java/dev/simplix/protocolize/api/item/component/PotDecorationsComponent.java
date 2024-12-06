package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.data.ItemType;

import java.util.List;

public interface PotDecorationsComponent extends StructuredComponent {

    List<ItemType> getDecorations();

    void setDecorations(List<ItemType> decorations);

    static PotDecorationsComponent create(List<ItemType> decorations) {
        return Protocolize.getService(Factory.class).create(decorations);
    }

    interface Factory {

        PotDecorationsComponent create(List<ItemType> decorations);

    }


}
