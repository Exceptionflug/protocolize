package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.JukeboxSong;
import dev.simplix.protocolize.api.item.SoundEvent;

public interface JukeboxPlayableComponent extends StructuredComponent {

    String getIdentifier();

    void setIdentifier(String identifier);

    Integer getSongId();

    void setSongId(Integer songId);

    JukeboxSong getJukeboxSong();

    void setJukeboxSong(JukeboxSong jukeboxSong);

    boolean isShowInTooltip();

    void setShowInTooltip(boolean showInTooltip);

    static JukeboxPlayableComponent create(String identifier, Integer songId, JukeboxSong jukeboxSong, boolean showInTooltip) {
        return Protocolize.getService(Factory.class).create(identifier, songId, jukeboxSong, showInTooltip);
    }

    interface Factory {

        JukeboxPlayableComponent create(String identifier, Integer songId, JukeboxSong jukeboxSong, boolean showInTooltip);

    }

}
