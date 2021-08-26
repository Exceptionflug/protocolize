package dev.simplix.protocolize.api.player;

import dev.simplix.protocolize.api.inventory.PlayerInventory;

import java.util.UUID;

/**
 * Date: 26.08.2021
 *
 * @author Exceptionflug
 */
public interface ProtocolizePlayer {

    PlayerInventory proxyInventory();

    UUID uniqueId();

    void sendPacket(Object packet);

    void sendPacketToServer(Object packet);

}
