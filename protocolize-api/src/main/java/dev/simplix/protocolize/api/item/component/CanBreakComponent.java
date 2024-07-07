package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.BlockPredicate;

import java.util.List;

public interface CanBreakComponent extends StructuredComponent {

    List<BlockPredicate> getBlockPredicates();

    void setBlockPredicates(List<BlockPredicate> blockPredicates);

    boolean isShowInTooltip();

    void setShowInTooltip(boolean showInTooltip);

    static CanBreakComponent create(List<BlockPredicate> blockPredicates, boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(blockPredicates, showInTooltip);
    }

    interface Factory {

        CanBreakComponent create(List<BlockPredicate> blockPredicates, boolean showInTooltip);

    }

}
