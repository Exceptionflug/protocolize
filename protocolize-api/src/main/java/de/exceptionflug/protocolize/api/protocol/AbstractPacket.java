package de.exceptionflug.protocolize.api.protocol;

import de.exceptionflug.protocolize.api.CancelSendSignal;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

/**
 * This class can be used when implementing custom packets. Please note that custom implemented packets need to be registered by the {@link PacketRegistration}.
 */
public abstract class AbstractPacket extends DefinedPacket {

    private boolean cancelSend;

    @Override
    public void handle(final AbstractPacketHandler abstractPacketHandler) {
        if(isCancelSend())
            throw CancelSendSignal.INSTANCE;
    }

    @Deprecated
    public boolean isCancelSend() {
        return cancelSend;
    }

    /**
     * If set to true, the packet handler will throw an instance of {@link CancelSendSignal}. Since this is deprecated,
     * use the setCancelled(boolean cancelled) method of the {@link de.exceptionflug.protocolize.api.event.PacketReceiveEvent}
     * or {@link de.exceptionflug.protocolize.api.event.PacketSendEvent} instead.
     * @param cancelSend
     */
    @Deprecated
    public void setCancelSend(final boolean cancelSend) {
        this.cancelSend = cancelSend;
    }
}
