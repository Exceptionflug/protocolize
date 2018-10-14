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
            if (event.getPacket().getValue() == 0) {
                WorldModule.setInternalGamemode(event.getPlayer().getUniqueId(), Gamemode.SURVIVAL);
            } else if (event.getPacket().getValue() == 1) {
                WorldModule.setInternalGamemode(event.getPlayer().getUniqueId(), Gamemode.CREATIVE);
            } else if (event.getPacket().getValue() == 2) {
                WorldModule.setInternalGamemode(event.getPlayer().getUniqueId(), Gamemode.ADVENTURE);
            } else if (event.getPacket().getValue() == 3) {
                WorldModule.setInternalGamemode(event.getPlayer().getUniqueId(), Gamemode.SPECTATOR);
            }
        }
    }
}
