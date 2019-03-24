package de.exceptionflug.protocolize.world.adapter;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.world.Gamemode;
import de.exceptionflug.protocolize.world.WorldModule;
import net.md_5.bungee.protocol.packet.Login;

public class JoinGameAdapter extends PacketAdapter<Login> {

    public JoinGameAdapter() {
        super(Stream.DOWNSTREAM, Login.class);
    }

    @Override
    public void receive(final PacketReceiveEvent<Login> event) {
        WorldModule.setGamemode(event.getPlayer().getUniqueId(), Gamemode.getByID(event.getPacket().getGameMode()));
    }
}
