package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.BlockPosition;
import dev.simplix.protocolize.api.Protocolize;

public interface LodestoneTrackerComponent extends StructuredComponent {

    String getDimension();

    void setDimension(String dimension);

    BlockPosition getPosition();

    void setPosition(BlockPosition position);

    boolean isTracked();

    void setTracked(boolean tracked);

    static LodestoneTrackerComponent create(boolean tracked) {
        return Protocolize.getService(Factory.class).create(tracked);
    }
    static LodestoneTrackerComponent create(String dimension, BlockPosition position, boolean tracked) {
        return Protocolize.getService(Factory.class).create(dimension, position, tracked);
    }

    interface Factory {

        LodestoneTrackerComponent create(boolean tracked);

        LodestoneTrackerComponent create(String dimension, BlockPosition position, boolean tracked);

    }

}
