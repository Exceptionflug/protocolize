package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface HideTooltipComponent extends StructuredComponent {

    static HideTooltipComponent create() {
        return Protocolize.getService(Factory.class).create();
    }

    interface Factory {

        HideTooltipComponent create();

    }

}
