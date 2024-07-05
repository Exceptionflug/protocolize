package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

import java.util.List;

public interface PotDecorationsComponent extends StructuredComponent {

    List<Integer> getDecorations();

    void setDecorations(List<Integer> decorations);

    static PotDecorationsComponent create(List<Integer> decorations) {
        return Protocolize.getService(Factory.class).create(decorations);
    }

    interface Factory {

        PotDecorationsComponent create(List<Integer> decorations);

    }


}
