package dev.simplix.protocolize.bungee;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.api.test.NamedSoundEffect;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
public class TestListener extends AbstractPacketListener<NamedSoundEffect> {

    public TestListener() {
        super(NamedSoundEffect.class, Direction.DOWNSTREAM, 0);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<NamedSoundEffect> event) {
        System.out.println(event.packet().sound());
        event.packet().pitch(2F);
        event.markForRewrite();
    }

    @Override
    public void packetSend(PacketSendEvent<NamedSoundEffect> event) {

    }

}
