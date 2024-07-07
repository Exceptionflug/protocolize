package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface NoteBlockSoundComponent extends StructuredComponent {

    String getSound();

    void setSound(String sound);

    static NoteBlockSoundComponent create(String sound) {
        return Protocolize.getService(Factory.class).create(sound);
    }

    interface Factory {

        NoteBlockSoundComponent create(String sound);

    }

}
