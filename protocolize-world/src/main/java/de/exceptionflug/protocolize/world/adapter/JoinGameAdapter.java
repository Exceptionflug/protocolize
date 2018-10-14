package de.exceptionflug.protocolize.world.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.world.WorldModule;
import de.exceptionflug.protocolize.world.packet.JoinGame;

public class JoinGameAdapter extends PacketAdapter<JoinGame> {

    public JoinGameAdapter() {
        super(Stream.DOWNSTREAM, JoinGame.class);
    }

    @Override
    public void receive(final PacketReceiveEvent<JoinGame> event) {
        WorldModule.setInternalGamemode(event.getPlayer().getUniqueId(), event.getPacket().getGamemode());
    }
}
