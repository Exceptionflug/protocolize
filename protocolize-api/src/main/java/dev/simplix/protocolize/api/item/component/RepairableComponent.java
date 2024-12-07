package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.util.Either;
import dev.simplix.protocolize.data.ItemType;

import java.util.List;

public interface RepairableComponent extends StructuredComponent {

    Either<String, List<ItemType>> getItems();

    void setItems(Either<String, List<ItemType>> items);

    static RepairableComponent create(List<ItemType> items) {
        return Protocolize.getService(Factory.class).create(items);
    }

    static RepairableComponent create(String itemResourceLocation) {
        return Protocolize.getService(Factory.class).create(itemResourceLocation);
    }

    interface Factory {

        RepairableComponent create(List<ItemType> items);

        RepairableComponent create(String itemResourceLocation);

    }

}
