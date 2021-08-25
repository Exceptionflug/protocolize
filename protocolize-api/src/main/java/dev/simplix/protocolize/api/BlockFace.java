package dev.simplix.protocolize.api;

public enum BlockFace {

    NONE(-1), BOTTOM(0), TOP(1), NORTH(2), SOUTH(3), WEST(4), EAST(5);

    private final int protocolId;

    BlockFace(final int protocolId) {
        this.protocolId = protocolId;
    }

    public static BlockFace blockFace(int protocolId) {
        for (BlockFace face : values()) {
            if (face.protocolId() == protocolId)
                return face;
        }
        return null;
    }

    public int protocolId() {
        return protocolId;
    }

}