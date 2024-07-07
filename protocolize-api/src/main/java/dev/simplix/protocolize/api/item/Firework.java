package dev.simplix.protocolize.api.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Firework {

    private int flightDuration;
    private List<Meta> explosions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        private Shape shape;
        private List<Integer> colors;
        private List<Integer> fadeColors;
        private boolean trail;
        private boolean twinkle;

        public enum Shape {
            SMALL_BALL,
            LARGE_BALL,
            STAR,
            CREEPER,
            BURST
        }

    }
}
