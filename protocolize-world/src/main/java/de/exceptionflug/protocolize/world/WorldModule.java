package de.exceptionflug.protocolize.world;

import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.world.adapter.ChangeGameStateAdapter;
import de.exceptionflug.protocolize.world.adapter.JoinGameAdapter;
import de.exceptionflug.protocolize.world.packet.ChangeGameState;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldModule {

    private static final Map<UUID, Gamemode> UUID_GAMEMODE_MAP = new HashMap<>();

    public static void initModule() {
        // TO_CLIENT
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_CLIENT, ChangeGameState.class, ChangeGameState.MAPPING);

        // Adapters
        ProtocolAPI.getEventManager().registerListener(new ChangeGameStateAdapter());
        ProtocolAPI.getEventManager().registerListener(new JoinGameAdapter());
    }

    public static Gamemode getGamemode(final UUID uuid) {
        return UUID_GAMEMODE_MAP.get(uuid);
    }

    public static void setInternalGamemode(final UUID uuid, final Gamemode gamemode) {
        UUID_GAMEMODE_MAP.put(uuid, gamemode);
    }

    public static void uncache(final UUID uuid) {
        UUID_GAMEMODE_MAP.remove(uuid);
    }

}
