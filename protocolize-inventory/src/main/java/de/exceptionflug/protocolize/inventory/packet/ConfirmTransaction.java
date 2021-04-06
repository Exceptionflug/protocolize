package de.exceptionflug.protocolize.inventory.packet;

import com.google.common.collect.Maps;
import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.api.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class ConfirmTransaction extends AbstractPacket {

  public static final Map<Integer, Integer> MAPPING_CLIENTBOUND = Maps.newHashMap();
  public static final Map<Integer, Integer> MAPPING_SERVERBOUND = Maps.newHashMap();

  static {
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_7_2, 0x32);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_7_6, 0x32);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_8, 0x32);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_9, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_9_1, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_9_2, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_9_3, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_10, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_11, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_11_1, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_12, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_12_1, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_12_2, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_13, 0x12);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_13_1, 0x12);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_13_2, 0x12);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_14, 0x12);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_1, 0x12);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_2, 0x12);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_3, 0x12);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_14_4, 0x12);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_15, 0x13);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_15_1, 0x13);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_15_2, 0x13);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_16, 0x12);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_16_1, 0x12);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_16_2, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_16_3, 0x11);
    MAPPING_CLIENTBOUND.put(MINECRAFT_1_16_4, 0x11);

    MAPPING_SERVERBOUND.put(MINECRAFT_1_7_2, 0x0F);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_7_6, 0x0F);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_8, 0x0F);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9, 0x05);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9_1, 0x05);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9_2, 0x05);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_9_3, 0x05);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_10, 0x05);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_11, 0x05);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_11_1, 0x05);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_12, 0x06);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_12_1, 0x05);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_12_2, 0x05);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_13, 0x06);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_13_1, 0x06);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_13_2, 0x06);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14, 0x07);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_1, 0x07);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_2, 0x07);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_3, 0x07);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_14_4, 0x07);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_15, 0x07);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_15_1, 0x07);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_15_2, 0x07);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_1, 0x07);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_2, 0x07);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_3, 0x07);
    MAPPING_SERVERBOUND.put(MINECRAFT_1_16_4, 0x07);
  }

  private int windowId;
  private short actionNumber;
  private boolean accepted;

  public ConfirmTransaction(final byte windowId, final short actionNumber, final boolean accepted) {
    this.windowId = windowId;
    this.actionNumber = actionNumber;
    this.accepted = accepted;
  }

  public ConfirmTransaction() {

  }

  public int getWindowId() {
    return windowId;
  }

  public void setWindowId(final int windowId) {
    this.windowId = windowId;
  }

  public short getActionNumber() {
    return actionNumber;
  }

  public void setActionNumber(final short actionNumber) {
    this.actionNumber = actionNumber;
  }

  public boolean isAccepted() {
    return accepted;
  }

  public void setAccepted(final boolean accepted) {
    this.accepted = accepted;
  }

  @Override
  public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    if ((protocolVersion >= MINECRAFT_1_12 || protocolVersion < MINECRAFT_1_8) && direction == Direction.TO_CLIENT)
      windowId = buf.readUnsignedByte();
    else
      windowId = buf.readByte();
    actionNumber = buf.readShort();
    accepted = buf.readBoolean();
    BufferUtil.finishBuffer(this, buf, direction, protocolVersion);
  }

  @Override
  public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    buf.writeByte(windowId);
    buf.writeShort(actionNumber);
    buf.writeBoolean(accepted);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ConfirmTransaction that = (ConfirmTransaction) o;
    return windowId == that.windowId &&
            actionNumber == that.actionNumber &&
            accepted == that.accepted;
  }

  @Override
  public int hashCode() {
    return Objects.hash(windowId, actionNumber, accepted);
  }

  @Override
  public String toString() {
    return "ConfirmTransaction{" +
            "windowId=" + windowId +
            ", actionNumber=" + actionNumber +
            ", accepted=" + accepted +
            '}';
  }
}
