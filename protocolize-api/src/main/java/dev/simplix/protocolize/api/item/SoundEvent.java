package dev.simplix.protocolize.api.item;


import dev.simplix.protocolize.data.Sound;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoundEvent {
    Sound sound;
    String identifier;
    Float fixedRange;
}
