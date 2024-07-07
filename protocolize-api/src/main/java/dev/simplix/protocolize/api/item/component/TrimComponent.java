package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.Trim;

public interface TrimComponent extends StructuredComponent {

    Trim getTrim();

    void setTrim(Trim trim);

    boolean isShowInTooltip();

    void setShowInTooltip(boolean showInTooltip);

    static TrimComponent create(Trim trim, boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(trim, showInTooltip);
    }

    interface Factory {

        TrimComponent create(Trim trim, boolean showInTooltip);

    }

}
