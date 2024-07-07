package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface MapPostProcessingComponent extends StructuredComponent {

    PostProcessingType getPostProcessingType();

    void setPostProcessingType(PostProcessingType postProcessingType);

    static MapPostProcessingComponent create(PostProcessingType postProcessingType) {
        return Protocolize.getService(Factory.class).create(postProcessingType);
    }

    enum PostProcessingType {
        LOCK,
        SCALE
    }


    interface Factory {

        MapPostProcessingComponent create(PostProcessingType postProcessingType);

    }


}
