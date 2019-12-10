package de.exceptionflug.protocolize.world.packet;

import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class PlayerLook extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING = new HashMap<>();

    static {
        MAPPING.put(MINECRAFT_1_8, 0x05);
        MAPPING.put(MINECRAFT_1_9, 0x0E);
        MAPPING.put(MINECRAFT_1_9_1, 0x0E);
        MAPPING.put(MINECRAFT_1_9_2, 0x0E);
        MAPPING.put(MINECRAFT_1_9_4, 0x0E);
        MAPPING.put(MINECRAFT_1_10, 0x0E);
        MAPPING.put(MINECRAFT_1_11, 0x0E);
        MAPPING.put(MINECRAFT_1_11_2, 0x0E);
        MAPPING.put(MINECRAFT_1_12, 0x10);
        MAPPING.put(MINECRAFT_1_12_1, 0x0F);
        MAPPING.put(MINECRAFT_1_12_2, 0x0F);
        MAPPING.put(MINECRAFT_1_13, 0x12);
        MAPPING.put(MINECRAFT_1_13_1, 0x12);
        MAPPING.put(MINECRAFT_1_13_2, 0x12);
        MAPPING.put(MINECRAFT_1_14, 0x13);
        MAPPING.put(MINECRAFT_1_14_1, 0x13);
        MAPPING.put(MINECRAFT_1_14_2, 0x13);
        MAPPING.put(MINECRAFT_1_14_3, 0x13);
        MAPPING.put(MINECRAFT_1_14_4, 0x13);
        MAPPING.put(MINECRAFT_1_15, 0x13);
    }

    private float yaw, pitch;
    private boolean onGround;

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }

    public void setOnGround(final boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public void read(final ByteBuf buf, final ProtocolConstants.Direction direction, final int protocolVersion) {
        yaw = buf.readFloat();
        pitch = buf.readFloat();
        onGround = buf.readBoolean();
    }

    @Override
    public void write(final ByteBuf buf, final ProtocolConstants.Direction direction, final int protocolVersion) {
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeBoolean(onGround);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PlayerLook that = (PlayerLook) o;
        return Float.compare(that.yaw, yaw) == 0 &&
                Float.compare(that.pitch, pitch) == 0 &&
                onGround == that.onGround;
    }

    @Override
    public int hashCode() {
        return Objects.hash(yaw, pitch, onGround);
    }

    @Override
    public String toString() {
        return "PlayerLook{" +
                "yaw=" + yaw +
                ", pitch=" + pitch +
                ", onGround=" + onGround +
                '}';
    }
}
