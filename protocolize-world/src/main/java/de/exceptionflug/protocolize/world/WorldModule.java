package de.exceptionflug.protocolize.world;

import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.world.adapter.*;
import de.exceptionflug.protocolize.world.packet.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WorldModule {

    private static final Map<UUID, Gamemode> UUID_GAMEMODE_MAP = new ConcurrentHashMap<>();
    private static final Map<UUID, Location> UUID_LOCATION_MAP = new ConcurrentHashMap<>();

    public static void initModule() {
        // TO_CLIENT
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_CLIENT, ChangeGameState.class, ChangeGameState.MAPPING);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_CLIENT, NamedSoundEffect.class, NamedSoundEffect.MAPPING);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_CLIENT, SignUpdate.class, SignUpdate.MAPPING_CLIENTBOUND);

        // TO_SERVER
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_SERVER, PlayerPosition.class, PlayerPosition.MAPPING);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_SERVER, PlayerLook.class, PlayerLook.MAPPING);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_SERVER, PlayerPositionLook.class, PlayerPositionLook.MAPPING);
        ProtocolAPI.getPacketRegistration().registerPacket(Protocol.GAME, ProtocolConstants.Direction.TO_SERVER, SignUpdate.class, SignUpdate.MAPPING_SERVERBOUND);

        // Adapters
        ProtocolAPI.getEventManager().registerListener(new ChangeGameStateAdapter());
        ProtocolAPI.getEventManager().registerListener(new JoinGameAdapter());
        ProtocolAPI.getEventManager().registerListener(new PlayerLookAdapter());
        ProtocolAPI.getEventManager().registerListener(new PlayerPositionAdapter());
        ProtocolAPI.getEventManager().registerListener(new PlayerPositionLookAdapter());
    }

    public static Gamemode getGamemode(final UUID uuid) {
        return UUID_GAMEMODE_MAP.get(uuid);
    }

    public static void playSound(final ProxiedPlayer proxiedPlayer, final Sound sound, final SoundCategory category, final float volume, final float pitch) {
        final NamedSoundEffect soundEffect = new NamedSoundEffect();
        soundEffect.setCategory(category);
        soundEffect.setPitch(pitch);
        soundEffect.setVolume(volume);
        soundEffect.setSound(sound);
        final Location location = getLocation(proxiedPlayer.getUniqueId());
        soundEffect.setX(location.getX());
        soundEffect.setY(location.getY());
        soundEffect.setZ(location.getZ());
        proxiedPlayer.unsafe().sendPacket(soundEffect);
    }

    public static Location getLocation(final UUID uuid) {
        return UUID_LOCATION_MAP.get(uuid);
    }

    public static void setLocation(final UUID uuid, final Location location) {
        UUID_LOCATION_MAP.put(uuid, location);
    }

    public static void setGamemode(final UUID uuid, final Gamemode gamemode) {
        UUID_GAMEMODE_MAP.put(uuid, gamemode);
    }

    public static void uncache(final UUID uuid) {
        UUID_GAMEMODE_MAP.remove(uuid);
        UUID_LOCATION_MAP.remove(uuid);
    }

}
