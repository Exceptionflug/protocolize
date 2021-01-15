package de.exceptionflug.protocolize.items.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.api.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class HeldItemChange extends AbstractPacket {

  public static final Map<Integer, Integer> MAPPING_CLIENTBOUND = Maps.newHashMap();
  public static final Map<Integer, Integer> MAPPING_SERVERBOUND = Maps.newHashMap();

  static {
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_8, 0x09);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_9, 0x37);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_9_1, 0x37);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_9_2, 0x37);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_9_3, 0x37);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_10, 0x37);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_11, 0x37);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_11_1, 0x37);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_12, 0x39);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_12_1, 0x3A);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_12_2, 0x3A);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_13, 0x3D);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_13_1, 0x3D);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_13_2, 0x3D);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_14, 0x3F);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_1, 0x3F);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_2, 0x3F);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_3, 0x3F);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_4, 0x3F);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_15, 0x40);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_15_1, 0x40);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_15_2, 0x40);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_16, 0x3F);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_16_1, 0x3F);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_16_2, 0x3F);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_16_3, 0x3F);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_16_4, 0x3F);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_16_5, 0x3F);

    MAPPING_SERVERBOUND.put(MINECRAFT_1_8, 0x09);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9, 0x17);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9_1, 0x17);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9_2, 0x17);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9_3, 0x17);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_10, 0x17);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_11, 0x17);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_11_1, 0x17);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_12, 0x1A);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_12_1, 0x1A);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_12_2, 0x1A);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_13, 0x21);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_13_1, 0x21);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_13_2, 0x21);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14, 0x23);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_1, 0x23);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_2, 0x23);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_3, 0x23);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_4, 0x23);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_15, 0x23);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_15_1, 0x23);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_15_2, 0x23);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16, 0x24);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_1, 0x24);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_2, 0x25);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_3, 0x25);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_4, 0x25);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_5, 0x25);
  }

  private short newSlot;

  public HeldItemChange(final short newSlot) {
    this.newSlot = newSlot;
  }

  public HeldItemChange() {
  }

  @Override
  public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    if (direction == Direction.TO_SERVER)
      newSlot = buf.readShort();
    else
      newSlot = buf.readByte();
    BufferUtil.finishBuffer(this, buf, direction, protocolVersion);
  }

  @Override
  public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    if (direction == Direction.TO_SERVER)
      buf.writeShort(newSlot);
    else
      buf.writeByte(newSlot);
  }

  public short getNewSlot() {
    return newSlot;
  }

  public void setNewSlot(final short newSlot) {
    this.newSlot = newSlot;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HeldItemChange that = (HeldItemChange) o;
    return newSlot == that.newSlot;
  }

  @Override
  public String toString() {
    return "HeldItemChange{" +
            "newSlot=" + newSlot +
            '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(newSlot);
  }
}
