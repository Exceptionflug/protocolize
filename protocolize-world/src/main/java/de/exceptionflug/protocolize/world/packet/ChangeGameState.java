package de.exceptionflug.protocolize.world.packet;

import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.api.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class ChangeGameState extends AbstractPacket {

  public static final Map<Integer, Integer> MAPPING = new HashMap<>();

  static {
    MAPPING.put(MINECRAFT_1_7_2, 0x2B);
    MAPPING.put(MINECRAFT_1_7_6, 0x2B);
    MAPPING.put(MINECRAFT_1_8, 0x2B);
    MAPPING.put(MINECRAFT_1_9, 0x1E);
    MAPPING.put(MINECRAFT_1_9_1, 0x1E);
    MAPPING.put(MINECRAFT_1_9_2, 0x1E);
    MAPPING.put(MINECRAFT_1_9_3, 0x1E);
    MAPPING.put(MINECRAFT_1_10, 0x1E);
    MAPPING.put(MINECRAFT_1_11, 0x1E);
    MAPPING.put(MINECRAFT_1_11_1, 0x1E);
    MAPPING.put(MINECRAFT_1_12, 0x1E);
    MAPPING.put(MINECRAFT_1_12_1, 0x1E);
    MAPPING.put(MINECRAFT_1_12_2, 0x1E);
    MAPPING.put(MINECRAFT_1_13, 0x20);
    MAPPING.put(MINECRAFT_1_13_1, 0x20);
    MAPPING.put(MINECRAFT_1_13_2, 0x20);
    MAPPING.put(MINECRAFT_1_14, 0x1E);
    MAPPING.put(MINECRAFT_1_14_1, 0x1E);
    MAPPING.put(MINECRAFT_1_14_2, 0x1E);
    MAPPING.put(MINECRAFT_1_14_3, 0x1E);
    MAPPING.put(MINECRAFT_1_14_4, 0x1E);
    MAPPING.put(MINECRAFT_1_15, 0x1F);
    MAPPING.put(MINECRAFT_1_15_1, 0x1F);
    MAPPING.put(MINECRAFT_1_15_2, 0x1F);
    MAPPING.put(MINECRAFT_1_16, 0x1E);
    MAPPING.put(MINECRAFT_1_16_1, 0x1E);
    MAPPING.put(MINECRAFT_1_16_2, 0x1D);
    MAPPING.put(MINECRAFT_1_16_3, 0x1D);
    MAPPING.put(MINECRAFT_1_16_4, 0x1D);
    MAPPING.put(MINECRAFT_1_17, 0x1E);
  }

  private Reason reason;
  private float value;

  public ChangeGameState(final Reason reason, final float value) {
    this.reason = reason;
    this.value = value;
  }

  public ChangeGameState() {

  }

  public Reason getReason() {
    return reason;
  }

  public void setReason(final Reason reason) {
    this.reason = reason;
  }

  public float getValue() {
    return value;
  }

  public void setValue(final float value) {
    this.value = value;
  }

  @Override
  public void read(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    reason = Reason.getById(buf.readUnsignedByte());
    value = buf.readFloat();
    BufferUtil.finishBuffer(this, buf, direction, protocolVersion);
  }

  @Override
  public void write(final ByteBuf buf, final Direction direction, final int protocolVersion) {
    buf.writeByte(((byte) reason.getId()) & 0xFF);
    buf.writeFloat(value);
  }

  @Override
  public String toString() {
    return "ChangeGameState{" +
            "reason=" + reason +
            ", value=" + value +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeGameState that = (ChangeGameState) o;
    return Float.compare(that.value, value) == 0 &&
            reason == that.reason;
  }

  @Override
  public int hashCode() {
    return Objects.hash(reason, value);
  }

  public enum Reason {

    INVALID_BED(0), END_RAINING(1), BEGIN_RAINING(2), CHANGE_GAMEMODE(3), EXIT_END(4), DEMO_MESSAGE(5), ARROW_HITTING_PLAYER(6), FADE_VALUE(7), FADE_TIME(8), ELDER_GUARDIAN_APPEARANCE(10);

    private final int id;

    Reason(final int id) {
      this.id = id;
    }

    public static Reason getById(final int id) {
      for (final Reason r : values()) {
        if (r.getId() == id)
          return r;
      }
      return null;
    }

    public int getId() {
      return id;
    }

  }

}
