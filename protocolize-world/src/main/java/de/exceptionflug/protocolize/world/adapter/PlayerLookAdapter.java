package de.exceptionflug.protocolize.world.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.world.Location;
import de.exceptionflug.protocolize.world.WorldModule;
import de.exceptionflug.protocolize.world.packet.PlayerLook;

public class PlayerLookAdapter extends PacketAdapter<PlayerLook> {

    public PlayerLookAdapter() {
        super(Stream.UPSTREAM, PlayerLook.class);
    }

    @Override
    public void receive(final PacketReceiveEvent<PlayerLook> event) {
        if(event.getPlayer() == null)
            return;
        final PlayerLook playerLook = event.getPacket();
        final Location location = WorldModule.getLocation(event.getPlayer().getUniqueId());
        if(location == null) {
            WorldModule.setLocation(event.getPlayer().getUniqueId(), new Location(0,0,0, playerLook.getYaw(), playerLook.getPitch()));
            return;
        }
        location.setYaw(playerLook.getYaw());
        location.setPitch(playerLook.getPitch());
    }

}
