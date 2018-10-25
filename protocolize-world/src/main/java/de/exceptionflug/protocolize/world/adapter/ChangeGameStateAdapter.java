package de.exceptionflug.protocolize.world.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.world.Gamemode;
import de.exceptionflug.protocolize.world.WorldModule;
import de.exceptionflug.protocolize.world.packet.ChangeGameState;
import de.exceptionflug.protocolize.world.packet.ChangeGameState.Reason;

public class ChangeGameStateAdapter extends PacketAdapter<ChangeGameState> {

    public ChangeGameStateAdapter() {
        super(Stream.DOWNSTREAM, ChangeGameState.class);
    }

    @Override
    public void receive(final PacketReceiveEvent<ChangeGameState> event) {
        if(event.getPacket().getReason() == Reason.CHANGE_GAMEMODE) {
            WorldModule.setInternalGamemode(event.getPlayer().getUniqueId(), Gamemode.getByID((int) event.getPacket().getValue()));
        }
    }
}
