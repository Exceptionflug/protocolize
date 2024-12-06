package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface TooltipStyleComponent extends StructuredComponent {

    String getStyle();

    void setStyle(String style);

    static TooltipStyleComponent create(String style) {
        return Protocolize.getService(Factory.class).create(style);
    }

    interface Factory {

        TooltipStyleComponent create(String style);

    }

}
