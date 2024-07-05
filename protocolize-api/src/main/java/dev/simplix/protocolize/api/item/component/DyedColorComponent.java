package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface DyedColorComponent extends StructuredComponent {

    int getColor();

    void setColor(int color);

    boolean isShowInTooltip();

    void setShowInTooltip(boolean showInTooltip);

    static DyedColorComponent create(int color) {
        return Protocolize.getService(Factory.class).create(color);
    }

    static DyedColorComponent create(int color, boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(color, showInTooltip);
    }

    interface Factory {

        DyedColorComponent create(int color);

        DyedColorComponent create(int color, boolean showInTooltip);

    }


}
