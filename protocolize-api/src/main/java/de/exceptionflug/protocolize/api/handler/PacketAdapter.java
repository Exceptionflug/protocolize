package de.exceptionflug.protocolize.api.handler;

import de.exceptionflug.protocolize.api.event.PacketReceiveEvent;
import de.exceptionflug.protocolize.api.event.PacketSendEvent;
import de.exceptionflug.protocolize.api.protocol.Stream;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.DefinedPacket;

/**
 * A PacketAdapter is a {@link PacketListener} used for listening to {@link DefinedPacket}s.
 *
 * @param <T> the packet type
 */
public class PacketAdapter<T extends DefinedPacket> implements PacketListener<T> {

  private final Stream stream;
  private final Class<T> packet;
  private final byte priority;

  /**
   * Creates a new PacketAdapter for the given stream and listening to the given packet with normal {@link EventPriority}
   *
   * @param stream the stream to listen
   * @param packet the desired packet class
   */
  public PacketAdapter(final Stream stream, final Class<T> packet) {
    this(stream, packet, EventPriority.NORMAL);
  }

  /**
   * Creates a new PacketAdapter for the given stream and listening to the given packet
   *
   * @param stream   the stream to listen
   * @param packet   the desired packet class
   * @param priority {@link EventPriority} when using multiple {@link PacketListener} for the same packet type
   */
  public PacketAdapter(final Stream stream, final Class<T> packet, final byte priority) {
    this.stream = stream;
    this.packet = packet;
    this.priority = priority;
  }

  public void receive(final PacketReceiveEvent<T> event) {
    // To be overwritten
  }

  public void send(final PacketSendEvent<T> event) {
    // To be overwritten
  }

  public Stream getStream() {
    return stream;
  }

  public Class<T> getPacketClass() {
    return packet;
  }

  @Override
  public byte getPriority() {
    return priority;
  }

}
