package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.BaseItemStack;

public interface UseRemainderComponent extends StructuredComponent {

    BaseItemStack getConvertInto();

    void setConvertInto(BaseItemStack convertInto);

    static UseRemainderComponent create(BaseItemStack convertInto) {
        return Protocolize.getService(UseRemainderComponent.Factory.class).create(convertInto);
    }

    interface Factory {

        UseRemainderComponent create(BaseItemStack convertInto);

    }

}
