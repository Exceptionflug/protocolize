package de.exceptionflug.protocolize.items;

public abstract class AbstractCustomItemIDMapping extends ItemIDMapping {

    public AbstractCustomItemIDMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final int id) {
        super(protocolVersionRangeStart, protocolVersionRangeEnd, id);
    }

    public abstract void apply(final ItemStack stack, final int protocolVersion);
    public abstract boolean isApplicable(final ItemStack stack, int version, int id, final int durability);

}
