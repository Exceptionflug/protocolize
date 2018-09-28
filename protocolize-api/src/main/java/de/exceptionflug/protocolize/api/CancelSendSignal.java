package de.exceptionflug.protocolize.api;

import java.lang.reflect.Field;

public final class CancelSendSignal {

    public final static Error INSTANCE;

    static {
        Error instance = null;
        try {
            final Class cancelSendSignalClass = Class.forName("net.md_5.bungee.connection.CancelSendSignal");
            final Field instanceField = cancelSendSignalClass.getField("INSTANCE");
            instance = (Error) instanceField.get(null);
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            INSTANCE = instance;
        }
    }

    private CancelSendSignal() {}

}
