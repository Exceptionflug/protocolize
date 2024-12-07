package dev.simplix.protocolize.api.item;

import dev.simplix.protocolize.api.chat.ChatElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JukeboxSong {
    private SoundEvent soundEvent;
    private ChatElement<?> description;
    private float duration;
    private int outputStrength;
}
