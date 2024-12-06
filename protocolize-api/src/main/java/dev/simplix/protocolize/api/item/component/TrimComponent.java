package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.TrimMaterial;
import dev.simplix.protocolize.api.item.TrimPattern;
import dev.simplix.protocolize.data.ItemType;

public interface TrimComponent extends StructuredComponent {

    ItemType getMaterialItem();

    void setMaterialItem(ItemType materialItem);

    TrimMaterial getTrimMaterial();

    void setTrimMaterial(TrimMaterial trim);

    ItemType getPatternItem();

    void setPatternItem(ItemType patternItem);

    TrimPattern getTrimPattern();

    void setTrimPattern(TrimPattern trim);

    boolean isShowInTooltip();

    void setShowInTooltip(boolean showInTooltip);

    static TrimComponent create(ItemType materialItem, TrimMaterial trimMaterial, ItemType patternItem, TrimPattern trimPattern, boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(materialItem, trimMaterial, patternItem, trimPattern, showInTooltip);
    }

    interface Factory {

        TrimComponent create(ItemType materialItem, TrimMaterial trimMaterial, ItemType patternItem, TrimPattern trimPattern, boolean showInTooltip);

    }

}
