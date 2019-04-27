package de.exceptionflug.protocolize.items;

public class SpawnEggItemIDMapping extends AbstractCustomItemIDMapping {

    private final String entityType;

    public SpawnEggItemIDMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final String entityType) {
        super(protocolVersionRangeStart, protocolVersionRangeEnd, 383);
        this.entityType = entityType;
    }


}
