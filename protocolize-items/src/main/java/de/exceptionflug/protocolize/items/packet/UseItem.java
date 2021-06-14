package de.exceptionflug.protocolize.items.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.Hand;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.api.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class UseItem extends AbstractPacket {

  public static final Map<Integer, Integer> MAPPING = Maps.newHashMap();

  static {
    MAPPING.put(MINECRAFT_1_9, 0x1D);
    MAPPING.put(MINECRAFT_1_9_1, 0x1D);
    MAPPING.put(MINECRAFT_1_9_2, 0x1D);
    MAPPING.put(MINECRAFT_1_9_3, 0x1D);
    MAPPING.put(MINECRAFT_1_10, 0x1D);
    MAPPING.put(MINECRAFT_1_11, 0x1D);
    MAPPING.put(MINECRAFT_1_11_1, 0x1D);
    MAPPING.put(MINECRAFT_1_12, 0x20);
    MAPPING.put(MINECRAFT_1_12_1, 0x20);
    MAPPING.put(MINECRAFT_1_12_2, 0x20);
    MAPPING.put(MINECRAFT_1_13, 0x2A);
    MAPPING.put(MINECRAFT_1_13_1, 0x2A);
    MAPPING.put(MINECRAFT_1_13_2, 0x2A);
    MAPPING.put(MINECRAFT_1_14, 0x2D);
    MAPPING.put(MINECRAFT_1_14_1, 0x2D);
    MAPPING.put(MINECRAFT_1_14_2, 0x2D);
    MAPPING.put(MINECRAFT_1_14_3, 0x2D);
    MAPPING.put(MINECRAFT_1_14_4, 0x2D);
    MAPPING.put(MINECRAFT_1_15, 0x2D);
    MAPPING.put(MINECRAFT_1_15_1, 0x2D);
    MAPPING.put(MINECRAFT_1_15_2, 0x2D);
    MAPPING.put(MINECRAFT_1_16, 0x2E);
    MAPPING.put(MINECRAFT_1_16_1, 0x2E);
    MAPPING.put(MINECRAFT_1_16_2, 0x2F);
    MAPPING.put(MINECRAFT_1_16_3, 0x2F);
    MAPPING.put(MINECRAFT_1_16_4, 0x2F);
    MAPPING.put(MINECRAFT_1_17, 0x2F);
  }

  private Hand hand;

  public UseItem(final Hand hand) {
    this.hand = hand;
  }

  public UseItem() {
  }

  public Hand getHand() {
    return hand;
  }

  public void setHand(final Hand hand) {
    this.hand = hand;
  }

  @Override
  public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    hand = Hand.getHandByID(readVarInt(buf));
    BufferUtil.finishBuffer(this, buf, direction, protocolVersion);
  }

  @Override
  public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    writeVarInt(hand.getProtocolId(), buf);
  }

  @Override
  public String toString() {
    return "UseItem{" +
            "hand=" + hand +
            '}';
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final UseItem useItem = (UseItem) o;
    return hand == useItem.hand;
  }

  @Override
  public int hashCode() {
    return Objects.hash(hand);
  }
}
