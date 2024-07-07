package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.BlockPredicate;

import java.util.List;

public interface CanPlaceOnComponent extends StructuredComponent {

    List<BlockPredicate> getBlockPredicates();

    void setBlockPredicates(List<BlockPredicate> blockPredicates);

    boolean isShowInTooltip();

    void setShowInTooltip(boolean showInTooltip);

    static CanPlaceOnComponent create(List<BlockPredicate> blockPredicates, boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(blockPredicates, showInTooltip);
    }

    interface Factory {

        CanPlaceOnComponent create(List<BlockPredicate> blockPredicates, boolean showInTooltip);

    }

}
