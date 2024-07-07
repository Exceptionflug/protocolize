package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface EnchantmentGlintComponent extends StructuredComponent {

    boolean isShowGlint();

    void setShowGlint(boolean showGlint);

    static EnchantmentGlintComponent create(boolean showGlint) {
        return Protocolize.getService(Factory.class).create(showGlint);
    }

    interface Factory {

        EnchantmentGlintComponent create(boolean showGlint);

    }

}
