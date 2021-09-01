package dev.simplix.protocolize.api;

/**
 * The direction in which a packet has been sent.<br>
 * SERVERBOUND means it is sent from the player to the server <br>
 * CLIENTBOUND means it is sent from the server to the player
 */
public enum PacketDirection {

    CLIENTBOUND, SERVERBOUND

}
