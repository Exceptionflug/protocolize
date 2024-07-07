package dev.simplix.protocolize.api.item;


import dev.simplix.protocolize.data.Sound;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoundEvent {
    private Sound sound;
    private String identifier;
    private Float fixedRange;
}
