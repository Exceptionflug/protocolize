package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface HideAdvancedTooltipComponent extends StructuredComponent {

    static HideAdvancedTooltipComponent create() {
        return Protocolize.getService(Factory.class).create();
    }

    interface Factory {

        HideAdvancedTooltipComponent create();

    }

}
