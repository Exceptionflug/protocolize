package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface UnbreakableComponent extends StructuredComponent {

    boolean isShowInTooltip();

    void setShowInTooltip(boolean showInTooltip);

    static UnbreakableComponent create(boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(showInTooltip);
    }

    interface Factory {

        UnbreakableComponent create(boolean showInTooltip);

    }

}
