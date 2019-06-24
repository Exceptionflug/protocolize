package de.exceptionflug.protocolize.world.packet;

import de.exceptionflug.protocolize.api.protocol.AbstractPacket;
import de.exceptionflug.protocolize.world.Location;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.exceptionflug.protocolize.api.util.ProtocolVersions.*;

public class PlayerPosition extends AbstractPacket {

    public static final Map<Integer, Integer> MAPPING = new HashMap<>();

    static {
        MAPPING.put(MINECRAFT_1_8, 0x04);
        MAPPING.put(MINECRAFT_1_9, 0x0C);
        MAPPING.put(MINECRAFT_1_9_1, 0x0C);
        MAPPING.put(MINECRAFT_1_9_2, 0x0C);
        MAPPING.put(MINECRAFT_1_9_4, 0x0C);
        MAPPING.put(MINECRAFT_1_10, 0x0C);
        MAPPING.put(MINECRAFT_1_11, 0x0C);
        MAPPING.put(MINECRAFT_1_11_2, 0x0C);
        MAPPING.put(MINECRAFT_1_12, 0x0E);
        MAPPING.put(MINECRAFT_1_12_1, 0x0D);
        MAPPING.put(MINECRAFT_1_12_2, 0x0D);
        MAPPING.put(MINECRAFT_1_13, 0x10);
        MAPPING.put(MINECRAFT_1_13_1, 0x10);
        MAPPING.put(MINECRAFT_1_13_2, 0x10);
        MAPPING.put(MINECRAFT_1_14, 0x11);
        MAPPING.put(MINECRAFT_1_14_1, 0x11);
        MAPPING.put(MINECRAFT_1_14_2, 0x11);
        MAPPING.put(MINECRAFT_1_14_3, 0x11);
    }

    private Location location;
    private boolean onGround;

    @Override
    public void read(final ByteBuf buf, final ProtocolConstants.Direction direction, final int protocolVersion) {
        final double x = buf.readDouble();
        final double y = buf.readDouble();
        final double z = buf.readDouble();
        onGround = buf.readBoolean();

        location = new Location(x, y, z);
    }

    @Override
    public void write(final ByteBuf buf, final ProtocolConstants.Direction direction, final int protocolVersion) {
        buf.writeDouble(location.getX());
        buf.writeDouble(location.getY());
        buf.writeDouble(location.getZ());
        buf.writeBoolean(onGround);
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setOnGround(final boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PlayerPosition playerPosition = (PlayerPosition) o;
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
