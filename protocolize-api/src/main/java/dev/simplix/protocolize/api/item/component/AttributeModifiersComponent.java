package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.Attribute;

import java.util.List;

public interface AttributeModifiersComponent extends StructuredComponent {

    List<Attribute> getAttributes();

    void setAttributes(List<Attribute> attributes);

    boolean isShowInTooltip();

    void setShowInTooltip(boolean showInTooltip);

    void addAttribute(Attribute attribute);

    void removeAttribute(Attribute attribute);

    void removeAllAttributes();

    static AttributeModifiersComponent create(List<Attribute> attributes) {
        return Protocolize.getService(Factory.class).create(attributes);
    }

    static AttributeModifiersComponent create(List<Attribute> attributes, boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(attributes, showInTooltip);
    }

    interface Factory {

        AttributeModifiersComponent create(List<Attribute> attributes);
        AttributeModifiersComponent create(List<Attribute> attributes, boolean showInTooltip);

    }

}
