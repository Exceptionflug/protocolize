package de.exceptionflug.protocolize.world.packet;

import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.api.util.BufferUtil;
import de.exceptionflug.protocolize.world.Location;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class PlayerPositionLook extends AbstractPacket {

  public static final Map<Integer, Integer> MAPPING = new HashMap<>();

  static {
    MAPPING.put(MINECRAFT_1_7_2, 0x06);
    MAPPING.put(MINECRAFT_1_7_6, 0x06);
    MAPPING.put(MINECRAFT_1_8, 0x06);
    MAPPING.put(MINECRAFT_1_9, 0x0D);
    MAPPING.put(MINECRAFT_1_9_1, 0x0D);
    MAPPING.put(MINECRAFT_1_9_2, 0x0D);
    MAPPING.put(MINECRAFT_1_9_4, 0x0D);
    MAPPING.put(MINECRAFT_1_10, 0x0D);
    MAPPING.put(MINECRAFT_1_11, 0x0D);
    MAPPING.put(MINECRAFT_1_11_2, 0x0D);
    MAPPING.put(MINECRAFT_1_12, 0x0F);
    MAPPING.put(MINECRAFT_1_12_1, 0x0E);
    MAPPING.put(MINECRAFT_1_12_2, 0x0E);
    MAPPING.put(MINECRAFT_1_13, 0x11);
    MAPPING.put(MINECRAFT_1_13_1, 0x11);
    MAPPING.put(MINECRAFT_1_13_2, 0x11);
    MAPPING.put(MINECRAFT_1_14, 0x12);
    MAPPING.put(MINECRAFT_1_14_1, 0x12);
    MAPPING.put(MINECRAFT_1_14_2, 0x12);
    MAPPING.put(MINECRAFT_1_14_3, 0x12);
    MAPPING.put(MINECRAFT_1_14_4, 0x12);
    MAPPING.put(MINECRAFT_1_15, 0x12);
    MAPPING.put(MINECRAFT_1_15_1, 0x12);
    MAPPING.put(MINECRAFT_1_15_2, 0x12);
    MAPPING.put(MINECRAFT_1_16, 0x13);
    MAPPING.put(MINECRAFT_1_16_1, 0x13);
    MAPPING.put(MINECRAFT_1_16_2, 0x13);
    MAPPING.put(MINECRAFT_1_16_3, 0x13);
    MAPPING.put(MINECRAFT_1_16_4, 0x13);
  }

  private Location location;
  private boolean onGround;

  @Override
  public void read(final ByteBuf buf, final ProtocolConstants.Direction direction, final int protocolVersion) {
    location = new Location(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat());
    onGround = buf.readBoolean();
    BufferUtil.finishBuffer(this, buf, direction, protocolVersion);
  }

  @Override
  public void write(final ByteBuf buf, final ProtocolConstants.Direction direction, final int protocolVersion) {
    buf.writeDouble(location.getX());
    buf.writeDouble(location.getY());
    buf.writeDouble(location.getZ());
    buf.writeFloat(location.getYaw());
    buf.writeFloat(location.getPitch());
    buf.writeBoolean(onGround);
  }

  public boolean isOnGround() {
    return onGround;
  }

  public void setOnGround(final boolean onGround) {
    this.onGround = onGround;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(final Location location) {
    this.location = location;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final PlayerPositionLook playerPosition = (PlayerPositionLook) o;
    return Objects.equals(location, playerPosition.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location);
  }

  @Override
  public String toString() {
    return "PlayerPosition{" +
            "location=" + location +
            '}';
  }
}
