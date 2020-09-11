package de.exceptionflug.protocolize.world.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.world.WorldModule;
import de.exceptionflug.protocolize.world.packet.PlayerPositionLook;

public class PlayerPositionLookAdapter extends PacketAdapter<PlayerPositionLook> {

  public PlayerPositionLookAdapter() {
    super(Stream.UPSTREAM, PlayerPositionLook.class);
  }

  @Override
  public void receive(final PacketReceiveEvent<PlayerPositionLook> event) {
    if (event.getPlayer() == null)
      return;
    final PlayerPositionLook packet = event.getPacket();
    WorldModule.setLocation(event.getPlayer().getUniqueId(), packet.getLocation());
  }

}
