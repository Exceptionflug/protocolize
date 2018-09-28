package de.exceptionflug.protocolize.api.protocol;

import de.exceptionflug.protocolize.api.CancelSendSignal;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public abstract class AbstractPacket extends DefinedPacket {

    private boolean cancelSend;

    @Override
    public void handle(final AbstractPacketHandler abstractPacketHandler) {
        if(isCancelSend())
            throw CancelSendSignal.INSTANCE;
    }

    public boolean isCancelSend() {
        return cancelSend;
    }

    public void setCancelSend(final boolean cancelSend) {
        this.cancelSend = cancelSend;
    }
}
