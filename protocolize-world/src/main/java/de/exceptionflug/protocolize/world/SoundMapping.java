package de.exceptionflug.protocolize.world;

public class SoundMapping {

    private final int protocolVersionRangeStart, protocolVersionRangeEnd;
    private final String soundName;

    public SoundMapping(int protocolVersionRangeStart, int protocolVersionRangeEnd, String soundName) {
        this.protocolVersionRangeStart = protocolVersionRangeStart;
        this.protocolVersionRangeEnd = protocolVersionRangeEnd;
        this.soundName = soundName;
    }

    public int getProtocolVersionRangeStart() {
        return protocolVersionRangeStart;
    }

    public int getProtocolVersionRangeEnd() {
        return protocolVersionRangeEnd;
    }

    public String getSoundName() {
        return soundName;
    }

}
