package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.BaseItemStack;

import java.util.List;

public interface BundleContentsComponent extends StructuredComponent {

    List<BaseItemStack> getItems();

    void setItems(List<BaseItemStack> items);

    void addItem(BaseItemStack itemStack);

    void removeItem(BaseItemStack itemStack);

    void removeAllItems();

    static BundleContentsComponent create(List<BaseItemStack> items) {
        return Protocolize.getService(Factory.class).create(items);
    }

    interface Factory {

        BundleContentsComponent create(List<BaseItemStack> items);

    }

}
