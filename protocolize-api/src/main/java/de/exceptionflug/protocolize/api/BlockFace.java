package de.exceptionflug.protocolize.api;

import net.md_5.bungee.api.ProxyServer;

public enum BlockFace {

    NONE(-1), BOTTOM(0), TOP(1), NORTH(2), SOUTH(3), WEST(4), EAST(5);

    private final int protocolId;

    BlockFace(final int protocolId) {
        this.protocolId = protocolId;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public static BlockFace getBlockFace(final int protocolId) {
        for(final BlockFace face : values()) {
            if(face.getProtocolId() == protocolId)
                return face;
        }
        ProxyServer.getInstance().getLogger().warning("[Protocolize] Unknown face with protocol id "+protocolId);
        return null;
    }

}
