package de.exceptionflug.protocolize.items;

public class SpawnEggItemIDMapping extends AbstractCustomItemIDMapping {

    private final String entityType;

    public SpawnEggItemIDMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final String entityType) {
        super(protocolVersionRangeStart, protocolVersionRangeEnd, 383);
        this.entityType = entityType;
    }

    @Override
    public void apply(ItemStack stack, int protocolVersion) {

    }

    @Override
    public boolean isApplicable(ItemStack stack, int version, int id, int durability) {
        return false;
    }
}
