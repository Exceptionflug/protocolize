package dev.simplix.protocolize.api.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Firework {

    int flightDuration;
    List<Meta> explosions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        Shape shape;
        List<Integer> colors;
        List<Integer> fadeColors;
        boolean trail;
        boolean twinkle;

        public enum Shape {
            SMALL_BALL,
            LARGE_BALL,
            STAR,
            CREEPER,
            BURST
        }

    }
}
